package com.zzs.iam.server.domain.model.captcha

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.iam.common.constants.UserAuthStrategy
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * @author 宋志宗 on 2022/8/26
 */
@Service
class CaptchaService(
  private val captchaStore: CaptchaStore
) {
  private val timeout = Duration.ofMinutes(5)
  private val maxFailure = 5

  /** 获取用户支持的验证策略 */
  suspend fun strategies(userId: String): List<UserAuthStrategy> {
    return emptyList()
  }

  /** 生成文本验证码 */
  suspend fun generate(key: String, userId: String) {
    val generate = com.zzs.iam.server.domain.model.captcha.Captcha.generate()
    captchaStore.save(key, generate, timeout)
  }

  /** 生成图片验证码 */
  suspend fun generateImage(key: String): com.zzs.iam.server.domain.model.captcha.ImageCaptcha {
    val generate = com.zzs.iam.server.domain.model.captcha.ImageCaptcha.generate()
    captchaStore.save(key, generate, timeout)
    return generate
  }

  /** 文本验证码认证 */
  suspend fun codeAuthenticate(key: String, code: String) {
    val captcha = captchaStore.get(key) ?: let {
      throw ResourceNotFoundException("验证码已过期")
    }
    if (captcha.code == code) {
      captchaStore.delete(key)
      return
    }
    val failCount = captcha.failCount
    if (failCount >= maxFailure) {
      captchaStore.delete(key)
      throw BadRequestException("验证码错误")
    }
    captcha.failCount = failCount + 1
    captchaStore.save(key, captcha, timeout)
    throw BadRequestException("验证码错误")
  }

  /** mfa验证码认证 */
  suspend fun mfaAuthenticate(userId: String) {

  }
}
