package com.zzs.iam.upms.domain.model.captcha;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2021/7/5
 */
public interface ImageCaptchaGenerator {

  /**
   * 生成图片验证码
   *
   * @return 图片验证码
   */
  @Nonnull
  ImageCaptcha generate();
}
