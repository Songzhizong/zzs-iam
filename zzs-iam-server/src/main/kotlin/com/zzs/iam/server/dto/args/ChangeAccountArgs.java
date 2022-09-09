package com.zzs.iam.server.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class ChangeAccountArgs {

  /**
   * 密码
   *
   * @required
   */
  @Nullable
  private String password;

  /**
   * 新账号
   *
   * @required
   */
  @Nullable
  private String newAccount;

  @Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nullable String password) {
    this.password = password;
  }

  @Nullable
  public String getNewAccount() {
    return newAccount;
  }

  public void setNewAccount(@Nullable String newAccount) {
    this.newAccount = newAccount;
  }
}
