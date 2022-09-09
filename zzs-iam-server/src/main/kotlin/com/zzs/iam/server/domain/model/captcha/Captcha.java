package com.zzs.iam.server.domain.model.captcha;

import com.zzs.framework.core.lang.RandomStringUtils;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/23
 */
public class Captcha {

  /** 验证码 */
  @Nonnull
  private String code = "";

  private int failCount = 0;

  /** 生成验证码 */
  @Nonnull
  public static Captcha generate() {
    Captcha captcha = new Captcha();
    captcha.setCode(RandomStringUtils.randomNumeric(6));
    return captcha;
  }

  @Nonnull
  public String getCode() {
    return code;
  }

  public void setCode(@Nonnull String code) {
    this.code = code;
  }

  public int getFailCount() {
    return failCount;
  }

  public void setFailCount(int failCount) {
    this.failCount = failCount;
  }
}
