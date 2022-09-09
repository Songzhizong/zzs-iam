package com.zzs.iam.server.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class ChangePasswordArgs {

  /**
   * 原密码
   *
   * @required
   */
  @Nullable
  private String oldPassword;

  /**
   * 新密码
   *
   * @required
   */
  @Nullable
  private String newPassword;

  /**
   * 确认新密码
   *
   * @required
   */
  @Nullable
  private String confirmationPassword;

  @Nullable
  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(@Nullable String oldPassword) {
    this.oldPassword = oldPassword;
  }

  @Nullable
  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(@Nullable String newPassword) {
    this.newPassword = newPassword;
  }

  @Nullable
  public String getConfirmationPassword() {
    return confirmationPassword;
  }

  public void setConfirmationPassword(@Nullable String confirmationPassword) {
    this.confirmationPassword = confirmationPassword;
  }
}
