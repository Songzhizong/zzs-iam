package com.zzs.iam.launcher.intergration

import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import com.zzs.iam.server.application.UserService
import com.zzs.iam.server.domain.model.user.AuthUser
import com.zzs.iam.server.domain.model.user.UserDO
import com.zzs.iam.server.domain.model.user.UserProvider
import com.zzs.iam.server.domain.model.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * @author 宋志宗 on 2022/8/16
 */
@Component
class IamUserProvider(
  private val userService: UserService,
  private val userRepository: UserRepository,
) : UserProvider {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(IamUserProvider::class.java)
  }


  override suspend fun authenticate(username: String, password: String): AuthUser {
    val authenticate = userService.authenticate(username, password)
    return userDo2AuthUser(authenticate)
  }

  override suspend fun getById(id: String): AuthUser {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    return userRepository.findById(id.toLong())?.let { userDo2AuthUser(it) }
      ?: let {
        log.info("{}通过用户ID: {} 获取用户信息为空", logPrefix, id)
        throw ResourceNotFoundException("用户不存在")
      }
  }

  override suspend fun findAllById(ids: Collection<String>): List<AuthUser> {
    val idSet = ids.mapTo(HashSet()) { it.toLong() }
    return userRepository.findAllById(idSet).map { userDo2AuthUser(it) }
  }

  override suspend fun getByPhone(phone: String): AuthUser {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    return userRepository.findByPhone(phone)?.let { userDo2AuthUser(it) }
      ?: let {
        log.info("{}通过手机号: {} 获取用户信息为空", logPrefix, phone)
        throw ResourceNotFoundException("用户不存在")
      }
  }

  private fun userDo2AuthUser(authenticate: UserDO): AuthUser {
    return AuthUser().also {
      it.userId = authenticate.id.toString()
      it.name = authenticate.name
      it.nickname = authenticate.nickname
      it.account = authenticate.account
      it.phone = authenticate.phone
      it.email = authenticate.email
      it.passwordDate = authenticate.passwordDate
    }
  }
}
