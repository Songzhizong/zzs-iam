package com.zzs.iam.upms.application

import cn.idealframework2.event.ReactiveEventPublisher
import cn.idealframework2.event.coroutine.publishAndAwait
import cn.idealframework2.exception.ForbiddenException
import cn.idealframework2.trace.coroutine.TraceContextHolder
import com.zzs.iam.common.event.user.UserLogined
import com.zzs.iam.upms.configure.IamServerProperties
import com.zzs.iam.upms.domain.model.authorization.Authentication
import com.zzs.iam.upms.domain.model.authorization.token.AccessToken
import com.zzs.iam.upms.domain.model.authorization.token.AccessTokenDO
import com.zzs.iam.upms.domain.model.authorization.token.AccessTokenStore
import com.zzs.iam.upms.domain.model.log.LoginLogDO
import com.zzs.iam.upms.domain.model.log.LoginLogRepository
import com.zzs.iam.upms.domain.model.org.AuthClientDO
import com.zzs.iam.upms.domain.model.org.PlatformUserRepository
import com.zzs.iam.upms.domain.model.user.AuthUser
import com.zzs.iam.upms.domain.model.user.UserProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author 宋志宗 on 2022/8/15
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class LoginService(
  private val userProvider: UserProvider,
  private val tokenStore: AccessTokenStore,
  private val properties: IamServerProperties,
  private val loginLogRepository: LoginLogRepository,
  private val reactiveEventPublisher: ReactiveEventPublisher,
  private val platformUserRepository: PlatformUserRepository,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(LoginService::class.java)
  }

  /** 密码登录 */
  suspend fun passwordLogin(
    authClient: AuthClientDO,
    username: String,
    password: String,
    rememberMe: Boolean,
    originalIp: String
  ): AccessToken {
    val user = userProvider.authenticate(username, password)
    return login(authClient, user, rememberMe, originalIp)
  }

  private suspend fun login(
    authClient: AuthClientDO,
    user: AuthUser,
    rememberMe: Boolean,
    originalIp: String
  ): AccessToken {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val platform = authClient.platform
    val userId = user.userId
    val userDo = platformUserRepository.findByPlatformAndUserId(platform, userId) ?: let {
      log.info(
        "{}登录失败, 用户: [{} {}] 不属于平台: {}",
        logPrefix, userId, user.name, platform
      )
      throw ForbiddenException("没有此平台的访问权限")
    }
    if (userDo.isFrozen) {
      log.info(
        "{}用户: [{} {}] 登录平台: {} 失败, 用户已被该平台冻结",
        logPrefix, userId, user.name, platform
      )
      throw ForbiddenException("用户已被当前平台冻结")
    }
    val clientId = authClient.clientId
    val repetitionLoginLimit = authClient.repetitionLoginLimit
    if (repetitionLoginLimit < 2) {
      tokenStore.cleanAllToken(clientId, userId)
    } else {
      tokenStore.keepLimit(clientId, userId, repetitionLoginLimit)
    }
    val passwordExpireDays = properties.passwordExpireDays
    val authentication = Authentication.crete(authClient, user, passwordExpireDays)
    val accessTokenDo = AccessTokenDO.create(rememberMe, authClient, authentication)
    tokenStore.saveAccessToken(accessTokenDo)
    val event = UserLogined()
      .also { it.platform = platform;it.userId = userId }
    reactiveEventPublisher.publishAndAwait(event)
    val loginLog = LoginLogDO.create(userId, platform, clientId, originalIp)
    loginLogRepository.save(loginLog)
    return accessTokenDo.toAccessToken()
  }
}
