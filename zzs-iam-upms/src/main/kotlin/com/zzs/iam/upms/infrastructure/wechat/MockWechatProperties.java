package com.zzs.iam.upms.infrastructure.wechat;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/24
 */
public class MockWechatProperties {

  /** 是否开启mock */
  private boolean enabled = false;

  /** mock open id */
  @Nonnull
  private String openId = "";

  /** mock 手机号码 */
  @Nonnull
  private String phoneNumber = "";

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Nonnull
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(@Nonnull String openId) {
    this.openId = openId;
  }

  @Nonnull
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(@Nonnull String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
