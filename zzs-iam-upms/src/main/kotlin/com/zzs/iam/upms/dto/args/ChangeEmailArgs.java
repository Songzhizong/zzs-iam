package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class ChangeEmailArgs {

  /**
   * 原密码
   *
   * @required
   */
  @Nullable
  private String password;

  /**
   * 新邮箱
   *
   * @required
   */
  @Nullable
  private String newEmail;

  @Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nullable String password) {
    this.password = password;
  }

  @Nullable
  public String getNewEmail() {
    return newEmail;
  }

  public void setNewEmail(@Nullable String newEmail) {
    this.newEmail = newEmail;
  }
}
