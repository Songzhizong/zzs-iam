package com.zzs.iam.upms.application

import cn.idealframework2.cache.coroutine.RedisCacheBuilderFactory
import cn.idealframework2.cache.serialize.StringValueSerializer
import cn.idealframework2.event.EventSuppliers
import cn.idealframework2.event.ReactiveTransactionalEventPublisher
import cn.idealframework2.event.coroutine.publishAndAwait
import cn.idealframework2.exception.ForbiddenException
import cn.idealframework2.exception.ResourceNotFoundException
import cn.idealframework2.trace.coroutine.TraceContextHolder
import com.zzs.iam.common.exception.MissTenantIdException
import com.zzs.iam.common.pojo.User
import com.zzs.iam.upms.domain.model.org.*
import com.zzs.iam.upms.domain.model.role.RoleRepository
import com.zzs.iam.upms.domain.model.user.UserProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

/**
 * 用户管理
 *
 * @author 宋志宗 on 2022/8/18
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class UserAffService(
  private val userProvider: UserProvider,
  private val roleRepository: RoleRepository,
  private val tenantRepository: TenantRepository,
  private val platformRepository: PlatformRepository,
  private val tenantUserRepository: TenantUserRepository,
  private val platformUserRepository: PlatformUserRepository,
  private val transactionalEventPublisher: ReactiveTransactionalEventPublisher,
  redisCacheBuilderFactory: RedisCacheBuilderFactory,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(UserAffService::class.java)
  }

  val userAvailableInTenantCache = redisCacheBuilderFactory
    .newBuilder<String, String>(StringValueSerializer.instance())
    .cacheNull(Duration.ofSeconds(60))
    .expireAfterWrite(Duration.ofMinutes(30))
    .multiLevel(1000, Duration.ofSeconds(60))
    .build("user_auth:available_in_tenant")

  /** 向平台添加用户 */
  suspend fun addPlatformUser(
    platform: String,
    userIds: Set<String>,
    roles: Collection<Long>?
  ): List<PlatformUserDO> {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val exists = platformUserRepository.findByPlatformAndUserIdIn(platform, userIds)
    val existsUserId = exists.mapTo(HashSet()) { it.userId }
    val filter = userIds.filter { !existsUserId.contains(it) }
    if (filter.isEmpty()) {
      return emptyList()
    }
    val platformDo = platformRepository.findByCode(platform) ?: let {
      log.info("{}添加用户失败, 平台: {} 不存在", logPrefix, platform)
      throw ResourceNotFoundException("添加用户失败, 平台不存在")
    }
    val users = userProvider.findAllById(filter)
    val list = users.map { PlatformUserDO.create(it, platform) }
    // 非多租户平台可分配角色
    if (!platformDo.isMultiTenant && !roles.isNullOrEmpty()) {
      val roleDos = roleRepository.findAllById(roles)
      val roleIdSet = roleDos
        .filter { it.platform == platform }
        .mapTo(LinkedHashSet()) { it.id }
      list.forEach { it.changeRoles(roleIdSet) }
    }
    platformUserRepository.saveAll(list)
    return list
  }

  /** 将用户从平台下移除 */
  suspend fun removePlatformUsers(platform: String, userIds: Set<String>) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val platformDo = platformRepository.findByCode(platform) ?: let {
      log.info("{}移除用户失败, 平台: {} 不存在", logPrefix, platform)
      throw ResourceNotFoundException("平台信息不存在")
    }
    if (platformDo.isMultiTenant) {
      if (tenantUserRepository.existsByPlatformAndUserIdIn(platform, userIds)) {
        log.info("{}从 {}平台移除用户失败, 因为用户尚存在某个租户下", logPrefix, platform)
        throw ForbiddenException("移除失败, 用户还在某个租户下")
      }
    }
    val users = platformUserRepository.findByPlatformAndUserIdIn(platform, userIds)
    platformUserRepository.deleteAll(users)
    log.info("{}成功从 {}平台下移除用户数据 {}条", logPrefix, platform, users.size)
  }

  /** 冻结平台用户 */
  suspend fun freezePlatformUser(platform: String, userIds: Set<String>) {
    val users = platformUserRepository.findByPlatformAndUserIdIn(platform, userIds)
    if (users.isEmpty()) {
      return
    }
    val suppliers = EventSuppliers.create()
    users.forEach { suppliers.add(it.freeze()) }
    platformUserRepository.saveAll(users)
    transactionalEventPublisher.publishAndAwait(suppliers)
  }

  /** 解冻平台用户 */
  suspend fun unfreezePlatformUser(platform: String, userIds: Set<String>) {
    val users = platformUserRepository.findByPlatformAndUserIdIn(platform, userIds)
    if (users.isEmpty()) {
      return
    }
    val suppliers = EventSuppliers.create()
    users.forEach { suppliers.add(it.unfreeze()) }
    platformUserRepository.saveAll(users)
    transactionalEventPublisher.publishAndAwait(suppliers)
  }

  /** 将用户添加到租户 */
  suspend fun addTenantUser(
    tenantId: Long,
    userIds: Set<String>,
    roles: Collection<Long>?,
    platform: String? = null
  ): List<TenantUserDO> {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val exists = tenantUserRepository.findByTenantIdAndUserIdIn(tenantId, userIds)
    val existsUserId = exists.mapTo(HashSet()) { it.userId }
    val filter = userIds.filter { !existsUserId.contains(it) }
    if (filter.isEmpty()) {
      return emptyList()
    }
    val tenantDo = tenantRepository.findById(tenantId) ?: let {
      log.info("{}添加用户失败, 租户 {} 不存在", logPrefix, tenantId)
      throw ResourceNotFoundException("添加用户失败, 租户不存在")
    }
    if (platform != tenantDo.platform) {
      log.info(
        "{}添加用户失败, 租户 [{} {}] 不属于平台: {}", logPrefix, tenantId, tenantDo.name, platform
      )
      throw ForbiddenException("没有此租户的管理权限")
    }
    val users = userProvider.findAllById(filter)
    val list = users.map { TenantUserDO.create(it, tenantDo) }
    if (!roles.isNullOrEmpty()) {
      val roleDos = roleRepository.findAllById(roles)
      val roleIdSet = roleDos
        .filter { it.tenantId == tenantId }
        .mapTo(LinkedHashSet()) { it.id }
      list.forEach { it.changeRoles(roleIdSet) }
    }
    tenantUserRepository.saveAll(list)
    return list
  }

  /** 批量移除用户 */
  suspend fun removeTenantUsers(tenantId: Long, userIds: Set<String>) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val users = tenantUserRepository.findByTenantIdAndUserIdIn(tenantId, userIds)
    val keys = users.map { genAvailableInTenantKey(it.userId, tenantId) }
    userAvailableInTenantCache.invalidateAll(keys)
    tenantUserRepository.deleteAll(users)
    log.info("{}成功从租户: {} 下移除用户数据 {}条", logPrefix, tenantId, users.size)
    @Suppress("DeferredResultUnused")
    coroutineScope { async { delay(2000); userAvailableInTenantCache.invalidateAll(keys) } }
  }

  /** 批量冻结冻结租户下的用户 */
  suspend fun freezeTenantUser(tenantId: Long, userIds: Set<String>) {
    val users = tenantUserRepository.findByTenantIdAndUserIdIn(tenantId, userIds)
    if (users.isEmpty()) {
      return
    }
    users.forEach { it.freeze() }
    val keys = users.map { genAvailableInTenantKey(it.userId, tenantId) }
    userAvailableInTenantCache.invalidateAll(keys)
    tenantUserRepository.saveAll(users)
    @Suppress("DeferredResultUnused")
    coroutineScope { async { delay(2000); userAvailableInTenantCache.invalidateAll(keys) } }
  }

  /** 批量解冻租户下的用户 */
  suspend fun unfreezeTenantUser(tenantId: Long, userIds: Set<String>) {
    val users = tenantUserRepository.findByTenantIdAndUserIdIn(tenantId, userIds)
    if (users.isEmpty()) {
      return
    }
    users.forEach { it.unfreeze() }
    tenantUserRepository.saveAll(users)
  }

  /** 获取用户所在的租户列表 */
  suspend fun userTenants(
    platform: String,
    userId: String
  ): List<com.zzs.iam.upms.dto.resp.UserTenantInfo> {
    val userTenants = tenantUserRepository.findAllByPlatformAndUserId(platform, userId)
    val tenantIds = userTenants.map { it.tenantId }
    val tenantDos = tenantRepository.findAllById(tenantIds)
    val tenantUserDoMap = userTenants.associateBy { it.tenantId }
    return tenantDos.map { tenant ->
      com.zzs.iam.upms.dto.resp.UserTenantInfo().also {
        it.tenantId = tenant.id
        it.tenantName = tenant.name
        it.isFrozen = tenantUserDoMap[tenant.id]?.isFrozen ?: false
      }
    }
  }

  /** 判断用户是否拥有某个租户的访问权限 */
  suspend fun isAvailableInTenant(userId: String, tenantId: Long): Boolean {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val key = genAvailableInTenantKey(userId, tenantId)
    return userAvailableInTenantCache.get(key) {
      return@get coroutineScope {
        val userAsync = async {
          tenantUserRepository.findByTenantIdAndUserId(tenantId, userId)
        }
        val tenantAsync = async { tenantRepository.findById(tenantId) }
        val userDo = userAsync.await()
        if (userDo == null || userDo.isFrozen) {
          log.info(
            "{}用户 {} 无法访问租户 {}, 因为用户不属于该租户或者已被该租户冻结",
            logPrefix, userId, tenantId
          )
          return@coroutineScope "0"
        }
        val tenantDo = tenantAsync.await()
        if (tenantDo == null || tenantDo.isFrozen) {
          log.info(
            "{}用户 {} 无法访问租户 {}, 因为租户不存在或者已被冻结", logPrefix, userId, tenantId
          )
          return@coroutineScope "0"
        }
        return@coroutineScope "1"
      }
    } == "1"
  }

  /**
   * 批量变更用户角色
   */
  @Suppress("DuplicatedCode")
  suspend fun changeRoles(
    platform: String,
    tenantId: Long?,
    userIds: Collection<String>,
    roles: Set<Long>
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val platformDo = platformRepository.findByCode(platform) ?: let {
      log.error("{}平台: {} 不存在", logPrefix, platform)
      throw ResourceNotFoundException("添加用户失败, 平台不存在")
    }
    // 租户下用户角色变更
    if (platformDo.isMultiTenant) {
      if (tenantId == null) {
        log.info("{}为多租户平台用户分配角色失败, 租户ID为空", logPrefix)
        throw MissTenantIdException()
      }
      val users = tenantUserRepository.findByTenantIdAndUserIdIn(tenantId, userIds)
      if (users.isEmpty()) {
        log.info("{}获取租户下用户信息列表为空", logPrefix)
        throw ResourceNotFoundException("获取用户信息为空")
      }

      if (roles.isEmpty()) {
        users.forEach { it.changeRoles(emptySet()) }
      } else {
        val roleDos = roleRepository.findAllById(roles)
        val roleIdSet = roleDos
          .filter { it.tenantId == tenantId }
          .mapTo(LinkedHashSet()) { it.id }
        users.forEach { it.changeRoles(roleIdSet) }
      }
      tenantUserRepository.saveAll(users)
      return
    }

    // 平台下用户角色变更
    val users = platformUserRepository.findByPlatformAndUserIdIn(platform, userIds)
    if (users.isEmpty()) {
      log.info("{}获取平台下用户信息列表为空", logPrefix)
      throw ResourceNotFoundException("获取用户信息为空")
    }
    if (roles.isEmpty()) {
      users.forEach { it.changeRoles(emptySet()) }
    } else {
      val roleDos = roleRepository.findAllById(roles)
      val roleIdSet = roleDos
        .filter { it.platform == platform }
        .mapTo(LinkedHashSet()) { it.id }
      users.forEach { it.changeRoles(roleIdSet) }
    }
    platformUserRepository.saveAll(users)
  }

  /** 同步账号 */
  suspend fun syncAccount(userId: String, account: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val c1 = tenantUserRepository.updateAccountByUserId(userId, account)
    val c2 = platformUserRepository.updateAccountByUserId(userId, account)
    if (c1 > 0) {
      log.info("{}用户 {} 的租户用户账号 {}条", logPrefix, userId, c1)
    }
    if (c2 > 0) {
      log.info("{}用户 {} 的平台用户账号 {}条", logPrefix, userId, c2)
    }
  }

  /** 同步手机号码 */
  suspend fun syncPhone(userId: String, phone: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val c1 = tenantUserRepository.updatePhoneByUserId(userId, phone)
    val c2 = platformUserRepository.updatePhoneByUserId(userId, phone)
    if (c1 > 0) {
      log.info("{}同步用户 {} 的租户用户手机号 {}条", logPrefix, userId, c1)
    }
    if (c2 > 0) {
      log.info("{}同步用户 {} 的平台用户手机号 {}条", logPrefix, userId, c2)
    }
  }

  /** 同步邮箱 */
  suspend fun syncEmail(userId: String, email: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val c1 = tenantUserRepository.updateEmailByUserId(userId, email)
    val c2 = platformUserRepository.updateEmailByUserId(userId, email)
    if (c1 > 0) {
      log.info("{}同步用户 {} 的租户用户邮箱 {}条", logPrefix, userId, c1)
    }
    if (c2 > 0) {
      log.info("{}同步用户 {} 的平台用户邮箱 {}条", logPrefix, userId, c2)
    }
  }

  /** 同步用户信息 */
  suspend fun syncUser(user: User) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userId = user.id.toString()
    coroutineScope {
      val async1 = async {
        val tenantUsers = tenantUserRepository.findAllByUserId(userId)
        if (tenantUsers.isNotEmpty()) {
          tenantUsers.forEach { it.update(user) }
          tenantUserRepository.saveAll(tenantUsers)
          log.info("{}同步用户 {} 的租户用户信息 {}条", logPrefix, userId, tenantUsers.size)
        }
      }
      val async2 = async {
        val platformUsers = platformUserRepository.findAllByUserId(userId)
        if (platformUsers.isNotEmpty()) {
          platformUsers.forEach { it.update(user) }
          platformUserRepository.saveAll(platformUsers)
          log.info("{}同步用户 {} 的平台用户信息 {}条", logPrefix, userId, platformUsers.size)
        }
      }
      async1.await()
      async2.await()
    }
  }

  private fun genAvailableInTenantKey(userId: String, tenantId: Long) = "$userId:$tenantId"
}
