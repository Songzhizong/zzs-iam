package com.zzs.iam.server.application

import com.github.benmanes.caffeine.cache.Caffeine
import com.zzs.framework.autoconfigure.cache.CacheProperties
import com.zzs.framework.core.cache.coroutine.RedisCacheBuilderFactory
import com.zzs.framework.core.cache.serialize.JsonValueSerializer
import com.zzs.framework.core.cache.serialize.StringValueSerializer
import com.zzs.framework.core.exception.ForbiddenException
import com.zzs.framework.core.exception.UnauthorizedException
import com.zzs.framework.core.lang.Sets
import com.zzs.iam.common.constants.IamHeaders
import com.zzs.iam.common.constants.RoleType
import com.zzs.iam.common.exception.MissTenantIdException
import com.zzs.iam.common.exception.NoTenantAccessException
import com.zzs.iam.common.pojo.SimpleMenu
import com.zzs.iam.server.domain.model.authorization.Authentication
import com.zzs.iam.server.domain.model.authorization.Authorization
import com.zzs.iam.server.domain.model.authorization.BearerAuthorization
import com.zzs.iam.server.domain.model.authorization.token.AccessTokenStore
import com.zzs.iam.server.domain.model.front.MenuRepository
import com.zzs.iam.server.domain.model.org.AuthClientRepository
import com.zzs.iam.server.domain.model.org.PlatformRepository
import com.zzs.iam.server.domain.model.org.PlatformUserRepository
import com.zzs.iam.server.domain.model.org.TenantUserRepository
import com.zzs.iam.server.domain.model.role.RoleMenuRelRepository
import com.zzs.iam.server.domain.model.role.RoleRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import java.time.Duration

/**
 * @author 宋志宗 on 2022/8/16
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserAuthService(
  private val userAffService: UserAffService,
  private val tokenStore: AccessTokenStore,
  private val twoStepService: TwoStepService,
  private val menuRepository: MenuRepository,
  private val roleRepository: RoleRepository,
  private val cacheProperties: CacheProperties,
  private val platformRepository: PlatformRepository,
  private val authClientRepository: AuthClientRepository,
  private val redisTemplate: ReactiveStringRedisTemplate,
  private val tenantUserRepository: TenantUserRepository,
  private val roleMenuRelRepository: RoleMenuRelRepository,
  private val platformUserRepository: PlatformUserRepository,
  redisCacheBuilderFactory: RedisCacheBuilderFactory
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(UserAuthService::class.java)
  }

  /** 缓存平台是否开启了API权限验证 */
  private val apiAuthCache = HashMap<String, Boolean>()

  /** 用户拥有权限的API接口缓存 */
  private val userApiCache = Caffeine.newBuilder()
    .expireAfterWrite(Duration.ofSeconds(60))
    .maximumSize(100000)
    .build<String, Boolean>()

  /** 用户拥有权限的菜单列表缓存 */
  private val userMenuCache = redisCacheBuilderFactory
    .newBuilder<String, List<SimpleMenu>>(object : JsonValueSerializer<List<SimpleMenu>>() {})
    .multiLevel(10000, Duration.ofSeconds(60))
    .expireAfterWrite(Duration.ofMinutes(30), Duration.ofMinutes(60))
    .build("user_auth:menus")

  /** 缓存用户是否为超管 0否 1是 */
  private val isAdminCache = redisCacheBuilderFactory
    .newBuilder<String, String>(StringValueSerializer.instance())
    .multiLevel(10000, Duration.ofSeconds(60))
    .expireAfterWrite(Duration.ofMinutes(30), Duration.ofMinutes(60))
    .build("user_auth:is_admin")

  /** 缓存用户是否拥有当前平台下所有菜单的权限 */
  private val hasAllMenuCache = redisCacheBuilderFactory
    .newBuilder<String, String>(StringValueSerializer.instance())
    .multiLevel(10000, Duration.ofSeconds(60))
    .expireAfterWrite(Duration.ofMinutes(30), Duration.ofMinutes(60))
    .build("user_auth:has_all_menu")

  init {
    runBlocking {
      val list = platformRepository.findAll()
      list.forEach { apiAuthCache[it.code] = it.isEnableApiAuthenticate }
    }
  }

  /** 退出登录 */
  suspend fun signOut(authorization: Authorization) {
    if (authorization is BearerAuthorization) {
      val token = authorization.token
      tokenStore.deleteAccessToken(token)
    }
  }

  /** 清理用户所有的access token */
  @Suppress("DuplicatedCode")
  suspend fun cleanAccessToken(userId: String) {
    val platformUserDoList = platformUserRepository.findAllByUserId(userId)
    if (platformUserDoList.isEmpty()) {
      return
    }
    val platforms = platformUserDoList.map { it.platform }
    val clients = authClientRepository.findAllByPlatformIn(platforms)
    if (clients.isEmpty()) {
      return
    }
    val clientIds = clients.map { it.clientId }
    coroutineScope {
      clientIds.map { async { tokenStore.cleanAllToken(it, userId) } }.forEach { it.await() }
    }
  }

  /** 清理用户在指定平台的access token */
  @Suppress("DuplicatedCode")
  suspend fun cleanAccessToken(platform: String, userId: String) {
    val clients = authClientRepository.findAllByPlatform(platform)
    if (clients.isEmpty()) {
      return
    }
    val clientIds = clients.map { it.clientId }
    coroutineScope {
      clientIds.map { async { tokenStore.cleanAllToken(it, userId) } }.forEach { it.await() }
    }
  }

  /** 通过授权信息获取用户的认证信息 */
  suspend fun loadAuthentication(authorization: Authorization): Authentication {
    if (authorization is BearerAuthorization) {
      return getAuthentication(authorization)
    }
    val name: String = authorization.javaClass.name
    log.error("未知的Authorization类型: {}", name)
    throw UnauthorizedException("Invalid authorization type: $name")
  }

  private suspend fun getAuthentication(authorization: BearerAuthorization): Authentication {
    val token = authorization.token
    val accessTokenDo = tokenStore.readAccessToken(token)
    if (accessTokenDo == null) {
      log.info("accessToken: {} 获取认证信息失败, 可能是token已过期", token)
      throw UnauthorizedException()
    }
    return accessTokenDo.authentication
  }

  suspend fun authenticate(
    authorizationHeader: String?,
    tenantId: Long?,
    requestPath: String?,
    apiAuthenticate: Boolean
  ): LinkedMultiValueMap<String, String> {
    if (authorizationHeader.isNullOrBlank()) {
      log.info("缺少 Authorization 请求头")
      throw UnauthorizedException("Authorization is blank")
    }
    val responseHeaders = LinkedMultiValueMap<String, String>()
    val authorization = Authorization.pare(authorizationHeader)

    // 1. 登录状态校验
    val authentication = loadAuthentication(authorization)
    val userId = authentication.userId
    val platform = authentication.platform
    val multiTenant = authentication.isMultiTenant
    responseHeaders.set(IamHeaders.PLATFORM, platform)
    responseHeaders.set(IamHeaders.MULTI_TENANT, multiTenant.toString())
    responseHeaders.set(IamHeaders.USER_ID, userId)

    // 2. 租户id校验
    if (multiTenant && tenantId != null) {
      // 验证用户是否该租户下
      val available = userAffService.isAvailableInTenant(userId, tenantId)
      if (!available) {
        log.info("认证失败: 用户 [{} {}] 没有租户: {} 的访问权限", platform, userId, tenantId)
        throw NoTenantAccessException()
      }
      responseHeaders.set(IamHeaders.TENANT_ID, tenantId.toString())
    }

    if (!requestPath.isNullOrBlank()) {
      // 3. 接口权限校验
      if (apiAuthenticate && apiAuthCache[platform] == true) {
        // api鉴权时, 多租户平台的租户ID不能为空
        if (multiTenant && tenantId == null) {
          log.info("认证失败: 未在请求头中携带租户id")
          throw MissTenantIdException()
        }
        val hasApiAuth = hasApiAuth(platform, tenantId, userId, requestPath)
        if (!hasApiAuth) {
          log.info(
            "认证失败: 用户 [{} {} {}] 没有此接口的访问权限: {}",
            platform, tenantId, userId, requestPath
          )
          throw ForbiddenException()
        }
      }

      // 4. 两步验证校验
      if (!multiTenant || tenantId != null) {
        twoStepService.verify(platform, tenantId, userId, requestPath)
      }
    }
    return responseHeaders
  }

  /** 判断用户是否拥有某个接口的访问权限 */
  suspend fun hasApiAuth(platform: String, tenantId: Long?, userId: String, path: String): Boolean {
    // 一级缓存
    val memoryKey = genUserApiMemoryCacheKey(platform, tenantId, userId, path)
    var result = userApiCache.getIfPresent(memoryKey)
    if (result != null) {
      return result
    }
    // 二级缓存
    val key = genUserApiRedisCacheKey(userId, platform, tenantId)
    if (!redisTemplate.hasKey(key).awaitSingle()) {
      //  如果缓存已失效 则刷新缓存
      refreshApiCache(userId, platform, tenantId)
    }
    result = redisTemplate.opsForSet().isMember(key, path).awaitSingle() == true
    userApiCache.put(memoryKey, result)
    return result
  }

  /** 刷新用户接口权限缓存 */
  suspend fun refreshApiCache(userId: String, platform: String, tenantId: Long?) {
    val empty = "::$$::empty::$$::"
    val timeout = Duration.ofMinutes(30)
    val key = genUserApiRedisCacheKey(userId, platform, tenantId)
    // 判断用户是否拥有平台内所有菜单的权限, 如果是则将平台内所有接口的api刷入缓存
    val hasAllMenu = hasAllMenu(platform, tenantId, userId)
    if (hasAllMenu) {
      val apis = menuRepository.findApisByPlatform(platform)
      if (apis.isEmpty()) {
        log.info("用户: [{} {} {}] 从平台下没有任何API权限数据", platform, tenantId, userId)
        redisTemplate.delete(key).awaitSingle()
        redisTemplate.opsForSet().add(key, empty).awaitSingle()
        redisTemplate.expire(key, timeout).awaitSingle()
        return
      }
      log.info(
        "用户: [{} {} {}]从平台下获取到 {}条API权限数据",
        platform, tenantId, userId, apis.size
      )
      redisTemplate.delete(key).awaitSingle()
      redisTemplate.opsForSet().add(key, *apis.toTypedArray()).awaitSingle()
      redisTemplate.expire(key, timeout).awaitSingle()
      return
    }
    // 获取用户拥有的角色id列表
    val roles = getUserRoleIds(platform, tenantId, userId)
    if (roles.isEmpty()) {
      log.info("refreshApiCache: 用户 [{} {} {}] 没有绑定任何角色", platform, tenantId, userId)
      redisTemplate.delete(key).awaitSingle()
      redisTemplate.opsForSet().add(key, empty).awaitSingle()
      redisTemplate.expire(key, timeout).awaitSingle()
      return
    }
    // 获取用户这些角色下的菜单ID列表
    val menuIds = roleMenuRelRepository
      .findAllByRoleIdIn(roles).mapTo(HashSet()) { it.menuId }
    if (menuIds.isEmpty()) {
      log.info("用户 [{} {} {}] 关联的菜单列表为空", platform, tenantId, userId)
      redisTemplate.delete(key).awaitSingle()
      redisTemplate.opsForSet().add(key, empty).awaitSingle()
      redisTemplate.expire(key, timeout).awaitSingle()
      return
    }
    // 将用户拥有权限的api列表刷入缓存
    val apis = menuRepository.findApisById(menuIds)
    redisTemplate.delete(key).awaitSingle()
    redisTemplate.opsForSet().add(key, *apis.toTypedArray()).awaitSingle()
    redisTemplate.expire(key, timeout).awaitSingle()
    log.info("用户 [{} {} {}] 拥有api权限 {}条", platform, tenantId, userId, apis.size)
  }

  suspend fun menus(
    userId: String,
    platform: String,
    tenantId: Long?,
    terminal: String
  ): List<SimpleMenu> {
    val key = genUserAuthCacheKey(userId, platform, tenantId)
    val menus = userMenuCache.get(key) {
      // 判断用户是否拥有平台内所有菜单的权限, 如果是则将所有菜单刷入缓存
      val hasAllMenu = hasAllMenu(platform, tenantId, userId)
      if (hasAllMenu) {
        val menus = menuRepository.findSimpleMenusByPlatform(platform)
        log.info(
          "用户: [{} {} {}] 拥有平台下所有菜单的访问权限 {}条",
          platform, tenantId, userId, menus.size
        )
        return@get menus
      }
      // 获取用户拥有的角色ID列表, 如果为空则代表用户没有任何菜单的权限
      val roles = getUserRoleIds(platform, tenantId, userId)
      if (roles.isEmpty()) {
        log.info("menus: 用户 [{} {} {}] 没有绑定任何角色", platform, tenantId, userId)
        return@get emptyList<SimpleMenu>()
      }
      // 获取这些角色下的菜单ID列表, 如果为空则代表用户没有任何菜单的权限
      val menuIds = roleMenuRelRepository
        .findAllByRoleIdIn(roles).mapTo(HashSet()) { it.menuId }
      if (menuIds.isEmpty()) {
        log.info("用户 [{} {} {}] 关联的菜单列表为空", platform, tenantId, userId)
        return@get emptyList()
      }
      // 获取用户拥有权限的菜单列表并刷入缓存
      val menus = menuRepository.findSimpleMenusById(menuIds)
      log.info("用户 [{} {} {}] 拥有 {}条菜单权限", platform, tenantId, userId, menus.size)
      return@get menus
    } ?: emptyList()
    if (menus.isEmpty()) {
      return emptyList()
    }
    //过滤出指定终端的菜单列表
    return menus.filter { it.terminal == terminal }
  }

  /** 刷新用户菜单缓存 */
  suspend fun refreshMenuCache(userId: String, platform: String, tenantId: Long?) {
    val key = genUserAuthCacheKey(userId, platform, tenantId)
    // 判断用户是否拥有平台内所有菜单的权限, 如果是则将所有菜单刷入缓存
    val hasAllMenu = hasAllMenu(platform, tenantId, userId)
    if (hasAllMenu) {
      val menus = menuRepository.findSimpleMenusByPlatform(platform)
      log.info(
        "用户: [{} {} {}] 拥有平台下所有菜单的访问权限 {}条",
        platform, tenantId, userId, menus.size
      )
      userMenuCache.put(key, menus)
      return
    }
    // 获取用户拥有的角色ID列表, 如果为空则将空集合刷入缓存
    val roles = getUserRoleIds(platform, tenantId, userId)
    if (roles.isEmpty()) {
      log.info("refreshMenuCache: 用户 [{} {} {}] 没有绑定任何角色", platform, tenantId, userId)
      userMenuCache.put(key, emptyList())
      return
    }
    // 获取这些角色下的菜单ID列表, 如果为空则将空集合刷入缓存
    val menuIds = roleMenuRelRepository
      .findAllByRoleIdIn(roles).mapTo(HashSet()) { it.menuId }
    if (menuIds.isEmpty()) {
      log.info("用户 [{} {} {}] 关联的菜单列表为空", platform, tenantId, userId)
      userMenuCache.put(key, emptyList())
      return
    }
    // 获取用户拥有权限的菜单列表并刷入缓存
    val menus = menuRepository.findSimpleMenusById(menuIds)
    log.info("用户 [{} {} {}] 拥有 {}条菜单权限", platform, tenantId, userId, menus.size)
    userMenuCache.put(key, menus)
  }

  /** 判断用户是否超管 */
  suspend fun isAdmin(platform: String, tenantId: Long?, userId: String): Boolean {
    val key = genUserAuthCacheKey(userId, platform, tenantId)
    return isAdminCache.get(key) {
      // 如果用户没有任何角色, 则代表用户不是超管
      val roleIds = getUserRoleIds(platform, tenantId, userId)
      if (roleIds.isEmpty()) {
        hasAllMenuCache.put(key, "0")
        return@get "0"
      }
      val roles = roleRepository.findAllById(roleIds)
      roles.forEach { role ->
        if (role.type == RoleType.ADMIN) {
          return@get "1"
        }
      }
      hasAllMenuCache.put(key, "0")
      return@get "0"
    } == "1"
  }

  /** 判断用户是否拥有所有菜单的权限 */
  suspend fun hasAllMenu(platform: String, tenantId: Long?, userId: String): Boolean {
    val key = genUserAuthCacheKey(userId, platform, tenantId)
    return hasAllMenuCache.get(key) {
      val roleIds = getUserRoleIds(platform, tenantId, userId)
      if (roleIds.isEmpty()) {
        isAdminCache.put(key, "0")
        return@get "0"
      }
      // 计算出用户是否为超管
      val roles = roleRepository.findAllById(roleIds)
      var isAdmin = false
      for (role in roles) {
        if (role.type == RoleType.ADMIN) {
          isAdmin = true
          break
        }
      }
      if (isAdmin) {
        // 如果是超管, 则更新用户是否超管缓存
        isAdminCache.put(key, "1")
        val platformDo = platformRepository.findByCode(platform) ?: return@get "0"
        // 如果是非多租户平台, 或者租户拥有所有菜单权限的平台, 则返回1  反之返回0
        if (!platformDo.isMultiTenant || platformDo.isTenantHasAllMenus) {
          return@get "1"
        } else {
          return@get "0"
        }
      } else {
        isAdminCache.put(key, "0")
        return@get "0"
      }
    } == "1"
  }

  /** 获取用户拥有权限的角色id列表 */
  private suspend fun getUserRoleIds(
    platform: String,
    tenantId: Long?,
    userId: String
  ) = coroutineScope {
    // 用户自身拥有的角色
    val userRolesAsync = async {
      if (tenantId != null) {
        tenantUserRepository.findByTenantIdAndUserId(tenantId, userId)?.roles ?: emptySet()
      } else {
        platformUserRepository.findByPlatformAndUserId(platform, userId)?.roles ?: emptySet()
      }
    }
    // 平台(租户)下的默认角色
    val basicRolesAsync = async {
      roleRepository.findAllBasic(platform, tenantId).map { it.id }
    }
    val userRoles = userRolesAsync.await()
    val basicRoles = basicRolesAsync.await()
    Sets.merge(userRoles, basicRoles)
  }

  private fun genUserAuthCacheKey(
    userId: String,
    platform: String,
    tenantId: Long?,
  ): String {
    return "$userId:$platform:${tenantId ?: "null"}"
  }

  private fun genUserApiMemoryCacheKey(
    platform: String,
    tenantId: Long?,
    userId: String,
    path: String
  ) = "$userId:$platform:${tenantId ?: "null"}$path"

  private fun genUserApiRedisCacheKey(
    userId: String,
    platform: String,
    tenantId: Long?
  ): String {
    val prefix = cacheProperties.formattedPrefix()
    return "${prefix}user_auth:api:$userId:$platform:${tenantId ?: "null"}"
  }
}
