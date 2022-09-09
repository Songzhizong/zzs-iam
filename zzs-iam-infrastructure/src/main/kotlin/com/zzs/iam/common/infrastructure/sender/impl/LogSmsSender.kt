package com.zzs.iam.common.infrastructure.sender.impl

import com.zzs.iam.common.infrastructure.sender.SmsSender
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
    log.info("向手机号: {} 发送短信验证码: {}", phone, code)
  }

}
