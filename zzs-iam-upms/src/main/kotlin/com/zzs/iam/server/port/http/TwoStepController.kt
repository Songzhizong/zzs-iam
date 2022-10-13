package com.zzs.iam.server.port.http

import cn.idealframework2.transmission.Result
import cn.idealframework2.utils.requireNotBlank
import com.zzs.iam.server.application.TwoStepService
import com.zzs.iam.server.infrastructure.security.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 两步验证
 *
 * @author 宋志宗 on 2022/8/26
 */
@RestController
@RequestMapping("/iam/two_step")
class TwoStepController(
  private val twoStepService: TwoStepService,
) {

  /**
   * 发送两步验证的邮箱验证码
   * <pre>
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   */
  @PostMapping("/sen_email_code")
  suspend fun sendEmailCode(): Result<Void> {
    val context = SecurityContextHolder.current()
    val userId = context.userId
    twoStepService.sendEmailCode(userId)
    return Result.success()
  }

  /**
   * 发送两步验证的短信验证码
   * <pre>
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   */
  @PostMapping("/sen_sms_code")
  suspend fun sendSmsCode(): Result<Void> {
    val context = SecurityContextHolder.current()
    val userId = context.userId
    twoStepService.sendSmsCode(userId)
    return Result.success()
  }

  /**
   * 验证码认证
   * <pre>
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   */
  @PostMapping("/code_authenticate")
  suspend fun codeAuthenticate(code: String?): Result<Void> {
    code.requireNotBlank { "验证码为空" }
    val context = SecurityContextHolder.current()
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    val userId = context.userId
    twoStepService.codeAuthenticate(platform, tenantId, userId, code)
    return Result.success()
  }

  @PostMapping("/mfa_authenticate")
  suspend fun mfaAuthenticate(code: String?): Result<Void> {
    code.requireNotBlank { "mfa code为空" }
    val context = SecurityContextHolder.current()
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    val userId = context.userId
    twoStepService.mfaAuthenticate(platform, tenantId, userId, code)
    return Result.success()
  }
}
