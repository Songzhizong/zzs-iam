package com.zzs.iam.server.infrastructure.sender

/**
 * 短信发送器
 *
 * @author 宋志宗 on 2022/8/26
 */
interface SmsSender {

  /** 发送短信验证码 */
  suspend fun sendCode(phone: String, code: String)
}
