package com.zzs.iam.upms.infrastructure.sender

/**
 * 邮件发送器
 *
 * @author 宋志宗 on 2022/8/26
 */
interface EmailSender {

  /**
   * 发送邮件验证码
   *
   * @param email     邮箱地址
   * @param operation 操作名
   * @param code      验证码
   * @author 宋志宗 on 2022/8/26
   */
  suspend fun sendMailCode(email: String, operation: String, code: String)
}
