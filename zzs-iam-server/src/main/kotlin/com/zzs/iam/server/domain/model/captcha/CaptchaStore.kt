package com.zzs.iam.server.domain.model.captcha

import java.time.Duration

/**
 * 验证码存储库接口
 *
 * @author 宋志宗 on 2022/8/23
 */
interface CaptchaStore {

  suspend fun save(key: String, captcha: com.zzs.iam.server.domain.model.captcha.Captcha, timeout: Duration)

  suspend fun delete(key: String)

  suspend fun get(key: String): com.zzs.iam.server.domain.model.captcha.Captcha?
}
