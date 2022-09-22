package com.zzs.iam.server.application

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.ForbiddenException
import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.framework.core.lang.StringUtils
import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.password.PasswordEncoder
import com.zzs.iam.server.domain.model.org.AuthClientDo
import com.zzs.iam.server.domain.model.org.AuthClientRepository
import com.zzs.iam.server.domain.model.org.PlatformRepository
import com.zzs.iam.server.dto.args.CreateAuthClientArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @author 宋志宗 on 2022/8/15
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class AuthClientService(
  private val passwordEncoder: PasswordEncoder,
  private val platformRepository: PlatformRepository,
  private val authClientRepository: AuthClientRepository
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(AuthClientService::class.java)
  }

  /** 创建客户端 */
  suspend fun create(args: CreateAuthClientArgs): AuthClientDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val platform = args.platform.requireNotBlank { "平台编码为空" }.let {
      platformRepository.findByCode(it) ?: run {
        log.info("{}平台: {} 不存在", logPrefix, it)
        throw ResourceNotFoundException("平台不存在")
      }
    }
    val clientId = args.clientId.requireNotBlank { "客户端ID为空" }.also {
      authClientRepository.findByClientId(it)?.also {
        throw BadRequestException("客户端ID已被使用")
      }
    }
    val clientSecret = args.clientSecret.requireNotBlank { "客户端密钥为空" }
    val name = args.name.requireNotBlank { "客户端名称为空" }
    val note = args.note
    val accessTokenValidity = args.accessTokenValidity
    val refreshTokenValidity = args.refreshTokenValidity
    val accessTokenAutoRenewal = args.accessTokenAutoRenewal
    val repetitionLoginLimit = args.repetitionLoginLimit
    val byteArray = "$clientId:$clientSecret".toByteArray(Charsets.UTF_8)
    val tokenValue = Base64.getUrlEncoder().encodeToString(byteArray)
    val encode = passwordEncoder.encode(clientSecret)
    val clientDo = AuthClientDo.create(
      platform, clientId, encode, name, note, accessTokenValidity, refreshTokenValidity,
      accessTokenAutoRenewal, repetitionLoginLimit, "Basic $tokenValue"
    )
    authClientRepository.save(clientDo)
    return clientDo
  }

  /** 删除客户端 */
  suspend fun delete(clientId: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    authClientRepository.findByClientId(clientId)?.also {
      authClientRepository.delete(it)
      log.info("{}成功删除客户端: {}", logPrefix, clientId)
    } ?: let {
      log.info("{}客户端: {} 不存在", logPrefix, clientId)
    }
  }

  /** 客户端认证 */
  suspend fun authenticate(authorization: String): AuthClientDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    if (!authorization.startsWith("Basic ")) {
      log.info("{}authClient认证失败, 客户端授权码不合法: {}", logPrefix, authorization)
      throw ForbiddenException("客户端授权码不合法")
    }
    val substring = authorization.substring(6)
    val string = Base64.getUrlDecoder().decode(substring).toString(Charsets.UTF_8)
    val split = StringUtils.split(string, ":", 2)
    if (split.size != 2) {
      log.info("{}authClient认证失败, 客户端授权码分割长度!=2: {}", logPrefix, authorization)
      throw ForbiddenException("客户端授权码不合法")
    }
    try {
      return authenticate(split[0], split[1])
    } catch (e: ForbiddenException) {
      log.info("{}{} {}", logPrefix, e.message, authorization)
      throw e
    }
  }

  /** 客户端认证 */
  suspend fun authenticate(clientId: String, clientSecret: String): AuthClientDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val authClientDo = authClientRepository.findByClientId(clientId) ?: let {
      log.info("{}客户端不存在: {}", logPrefix, clientId)
      throw ForbiddenException("客户端授权码不合法")
    }
    val encoded = authClientDo.clientSecret
    val matches = passwordEncoder.matches(clientSecret, encoded)
    if (matches) {
      return authClientDo
    }
    log.info("{}客户端密钥匹配不通过: {} - {}", logPrefix, clientId, clientSecret)
    throw ForbiddenException("客户端授权码不合法")
  }
}
