package com.zzs.iam.server.infrastructure.sender.impl

import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import com.zzs.iam.server.infrastructure.sender.SmsSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author 宋志宗 on 2022/8/26
 */
class LogSmsSender : SmsSender {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(LogSmsSender::class.java)
  }

  override suspend fun sendCode(phone: String, code: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    log.info("{}向手机号: {} 发送短信验证码: {}", logPrefix, phone, code)
  }

}
