package com.zzs.iam.server.port.http

import com.zzs.framework.core.transmission.ListResult
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.constants.IamHeaders
import com.zzs.iam.common.pojo.SimpleMenu
import com.zzs.iam.server.application.UserAffService
import com.zzs.iam.server.application.UserAuthService
import com.zzs.iam.server.domain.model.authorization.Authorization
import com.zzs.iam.server.domain.model.user.AuthUser
import com.zzs.iam.server.dto.resp.UserTenantInfo
import com.zzs.iam.server.infrastructure.security.SecurityContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

/**
 * 用户权限接口
 *
 * @author 宋志宗 on 2022/8/16
 */
@RestController
@RequestMapping("/iam/user_self")
class UserAuthController(
  private val tenantUserAffService: UserAffService,
  private val userAuthService: UserAuthService,
) {

  /**
   * 获取个人信息
   * <pre>
   *   不需要api鉴权
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @GetMapping("/information")
  suspend fun currentUser(exchange: ServerWebExchange): Result<AuthUser> {
    val authorization = Authorization.get(exchange)
    val authentication = userAuthService.loadAuthentication(authorization)
    val user = authentication.toUser().desensitize()
    return Result.success(user)
  }

  /**
   * 获取所在的租户信息列表
   * <pre>
   *   不需要api鉴权
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @GetMapping("/tenants")
  suspend fun currentUserTenants(exchange: ServerWebExchange): ListResult<UserTenantInfo> {
    val authorization = Authorization.get(exchange)
    val authentication = userAuthService.loadAuthentication(authorization)
    val platform = authentication.platform
    val userId = authentication.userId
    val userTenants = tenantUserAffService.userTenants(platform, userId)
    return ListResult.of(userTenants)
  }

  /**
   * 获取用户在指定终端下拥有权限的菜单列表
   * <pre>
   *   不需要api鉴权
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @GetMapping("/terminal_menus")
  suspend fun menus(terminal: String?): ListResult<SimpleMenu> {
    terminal.requireNotBlank { "终端id为空" }
    val context = SecurityContextHolder.current()
    val userId = context.userId
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    val menus = userAuthService.menus(userId, platform, tenantId, terminal)
    return ListResult.of(menus)
  }

  /**
   * 判断用户是否为超管
   * <pre>
   *   不需要api鉴权
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @GetMapping("/is_admin")
  suspend fun isAdmin(): Result<Boolean> {
    val context = SecurityContextHolder.current()
    val userId = context.userId
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    val admin = userAuthService.isAdmin(platform, tenantId, userId)
    return Result.success(admin)
  }

  /**
   * 退出登录
   * <pre>
   *   不需要api鉴权
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/sign_out")
  suspend fun signOut(exchange: ServerWebExchange): Result<Void> {
    val authorization = Authorization.get(exchange)
    userAuthService.signOut(authorization)
    return Result.success()
  }

  /**
   * 身份认证接口
   *
   * @ignore
   */
  @PostMapping("/authenticate")
  suspend fun authenticate(exchange: ServerWebExchange): ResponseEntity<Result<Any>> {
    val requestHeaders = exchange.request.headers
    val authorization = requestHeaders.getFirst(HttpHeaders.AUTHORIZATION)
    val requestPath = requestHeaders.getFirst(IamHeaders.REQUEST_PATH)
    val authenticateApi = requestHeaders.getFirst(IamHeaders.AUTHENTICATE_API)
    val tenantId = requestHeaders.getFirst(IamHeaders.TENANT_ID)?.toLong()
    val responseHeaders = userAuthService
      .authenticate(authorization, tenantId, requestPath, authenticateApi.toBoolean())
    return ResponseEntity(Result.success(), responseHeaders, HttpStatus.OK)
  }
}
