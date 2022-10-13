package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class ChangePhoneArgs {

  /**
   * 密码
   *
   * @required
   */
  @Nullable
  private String password;

  /**
   * 新手机号码
   *
   * @required
   */
  @Nullable
  private String newPhone;

  @Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nullable String password) {
    this.password = password;
  }

  @Nullable
  public String getNewPhone() {
    return newPhone;
  }

  public void setNewPhone(@Nullable String newPhone) {
    this.newPhone = newPhone;
  }
}
