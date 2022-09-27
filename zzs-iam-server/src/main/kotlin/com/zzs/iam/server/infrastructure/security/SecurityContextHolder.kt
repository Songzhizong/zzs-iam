package com.zzs.iam.server.infrastructure.security

import com.zzs.framework.core.exception.InternalServerException
import com.zzs.framework.core.exception.UnauthorizedException
import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

/**
 * @author 宋志宗 on 2022/8/16
 */
object SecurityContextHolder {
  private val log: Logger = LoggerFactory.getLogger(SecurityContextHolder::class.java)
  const val contextKey = "iam.securityContext"

  suspend fun optional(): SecurityContext? {
    return Mono.deferContextual {
      val hasKey = it.hasKey(contextKey)
      if (!hasKey) {
        return@deferContextual Mono.empty()
      }
      val value: Any = it[contextKey]
      if (value is SecurityContext) {
        return@deferContextual Mono.just(value)
      }
      Mono.error(InternalServerException("iam.securityContext not instance of com.zzs.iam.common.infrastructure.security.SecurityContext"))
    }.awaitSingleOrNull()
  }

  suspend fun current(): SecurityContext {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val securityContext = optional()
    if (securityContext == null) {
      log.info("{}获取securityContext返回null", logPrefix)
      throw UnauthorizedException()
    }
    return securityContext
  }
}
