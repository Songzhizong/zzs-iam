package com.zzs.iam.upms.port.event

import cn.idealframework2.event.coroutine.EventListenerManager
import cn.idealframework2.trace.coroutine.TraceContextHolder
import com.zzs.iam.common.event.user.*
import com.zzs.iam.upms.application.UserAffService
import com.zzs.iam.upms.application.UserAuthService
import com.zzs.iam.upms.domain.model.org.PlatformRepository
import com.zzs.iam.upms.domain.model.org.TenantUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 用户相关事件监听器
 *
 * @author 宋志宗 on 2022/8/17
 */
@Configuration("iamUserEventListener")
class UserEventListener(
  private val userAffService: UserAffService,
  private val userAuthService: UserAuthService,
  private val platformRepository: PlatformRepository,
  private val tenantUserRepository: TenantUserRepository,
  private val eventListenerManager: EventListenerManager
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(UserEventListener::class.java)
  }

  /** 用户被冻结时清理登录token */
  @Bean("iam.iamUserFrozen.cleanAuthToken")
  fun userFrozenListener() = eventListenerManager.listen(
    "iam.iamUserFrozen.cleanAuthToken",
    UserFrozen.TOPIC,
    UserFrozen::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userId = it.userId
    log.info("{}监听到用户 [{}] 被冻结事件, 清理其所有登录token", logPrefix, userId)
    userAuthService.cleanAccessToken(userId.toString())
  }

  /** 当用户的密码发生变更时, 需要清理到该用户所有的登录状态 */
  @Bean("iam.iamUserPasswordChanged.cleanAuthToken")
  fun userPasswordChangedListener() = eventListenerManager.listen(
    "iam.iamUserPasswordChanged.cleanAuthToken",
    UserPasswordChanged.TOPIC,
    UserPasswordChanged::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userId = it.userId
    log.info("{}监听到用户 [{}] 密码变更事件, 清理其所有登录token", logPrefix, userId)
    userAuthService.cleanAccessToken(userId.toString())
  }

  /** 用户登录账号发生变更时, 同步用户信息 */
  @Bean("iam.iamUserAccountChanged.syncUserInfo")
  fun userAccountChangedListener() = eventListenerManager.listen(
    "iam.iamUserAccountChanged.syncUserInfo",
    UserAccountChanged.TOPIC,
    UserAccountChanged::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userId = it.userId.toString()
    val account = it.account ?: ""
    log.info("{}监听到用户 [{}] 账号发生变更, 同步该用户的账号信息", logPrefix, userId)
    userAffService.syncAccount(userId, account)
  }

  /** 用户手机号发生变更时, 同步用户信息 */
  @Bean("iam.iamUserPhoneChanged.syncUserInfo")
  fun userPhoneChangedListener() = eventListenerManager.listen(
    "iam.iamUserPhoneChanged.syncUserInfo",
    UserPhoneChanged.TOPIC,
    UserPhoneChanged::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userId = it.userId.toString()
    val phone = it.phone ?: ""
    log.info("{}监听到用户 [{}] 手机号发生变更, 同步该用户的手机号信息", logPrefix, userId)
    userAffService.syncPhone(userId, phone)
  }

  /** 用户邮箱发生变更时, 同步用户信息 */
  @Bean("iam.iamUserEmailChanged.syncUserInfo")
  fun userEmailChangedListener() = eventListenerManager.listen(
    "iam.iamUserEmailChanged.syncUserInfo",
    UserEmailChanged.TOPIC,
    UserEmailChanged::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userId = it.userId.toString()
    val email = it.email ?: ""
    log.info("{}监听到用户 [{}] 邮箱发生变更, 同步该用户的邮箱信息", logPrefix, userId)
    userAffService.syncEmail(userId, email)
  }

  /** 监听到用户信息发生变更时, 同步用户信息 */
  @Bean("iam.iamUserUpdated.syncUserInfo")
  fun userChangedListener() = eventListenerManager.listen(
    "iam.iamUserUpdated.syncUserInfo",
    UserUpdated.TOPIC,
    UserUpdated::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val user = it.user ?: return@listen
    val userId = user.id.toString()
    val name = user.name
    log.info("{}监听到用户 [{} {}] 个人信息发生变更, 同步该用户的个人信息", logPrefix, userId, name)
    userAffService.syncUser(user)
  }

  /** 用户登录时刷新其相关的权限缓存 */
  @Bean("iam.iamUserLogined.refreshCache")
  fun userLoginedListener() = eventListenerManager.listen(
    "iam.iamUserLogined.refreshCache",
    UserLogined.TOPIC,
    UserLogined::class.java
  ) {
    val platform = it.platform
    val userId = it.userId
    val platformDo = platformRepository.findByCode(platform) ?: return@listen
    if (!platformDo.isMultiTenant) {
      userAuthService.refreshApiCache(userId, platform, null)
      userAuthService.refreshMenuCache(userId, platform, null)
      return@listen
    }
    val list = tenantUserRepository.findAllByPlatformAndUserId(platform, userId)
    if (list.isEmpty()) {
      return@listen
    }
    coroutineScope {
      list.map { tenantUserDo ->
        val tenantId = tenantUserDo.tenantId
        async {
          userAuthService.refreshApiCache(userId, platform, tenantId)
          userAuthService.refreshMenuCache(userId, platform, tenantId)
        }
      }.forEach { async -> async.await() }
    }
  }

  /** 用户被平台冻结时 清理用户在该平台下的登录信息 */
  @Bean("iam.iamPlatformUserFrozen.cleanAuthToken")
  fun platformUserFrozenListener() = eventListenerManager.listen(
    "iam.iamPlatformUserFrozen.cleanAuthToken",
    PlatformUserFrozen.TOPIC,
    PlatformUserFrozen::class.java
  ) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val platform = it.platform
    val user = it.user
    val userId = user.userId
    val name = user.name
    log.info(
      "{}监听到用户 [{} {}] 被平台: {} 冻结, 清理用户在该平台下的登录信息",
      logPrefix, userId, name, platform
    )
    userAuthService.cleanAccessToken(platform, userId)
  }
}
