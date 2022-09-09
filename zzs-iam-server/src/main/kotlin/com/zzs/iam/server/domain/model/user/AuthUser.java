package com.zzs.iam.server.domain.model.user;

import com.zzs.framework.core.utils.DesensitizeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;

/**
 * 用户对象
 *
 * @author 宋志宗 on 2022/8/16
 */
public class AuthUser {
  /** 用户唯一id */
  @Nonnull
  private String userId = "";

  /** 用户姓名 */
  @Nullable
  private String name;

  /** 昵称 */
  @Nullable
  private String nickname;

  /** 账号 */
  @Nullable
  private String account;

  /** 手机号 */
  @Nullable
  private String phone;

  /** 邮箱 */
  @Nullable
  private String email;

  /** 上次修改密码的时间 */
  @Nullable
  private LocalDate passwordDate;

  /**
   * 信息脱敏
   *
   * @author 宋志宗 on 2022/6/7
   */
  @Nonnull
  public AuthUser desensitize() {
    setAccount(DesensitizeUtils.desensitize(getAccount(), 2, 2, '*'));
    setPhone(DesensitizeUtils.desensitizePhone(getPhone()));
    setEmail(DesensitizeUtils.desensitizeEmail(getEmail()));
    return this;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public void setUserId(@Nonnull String userId) {
    this.userId = userId;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@Nullable String nickname) {
    this.nickname = nickname;
  }

  @Nullable
  public String getAccount() {
    return account;
  }

  public void setAccount(@Nullable String account) {
    this.account = account;
  }

  @Nullable
  public String getPhone() {
    return phone;
  }

  public void setPhone(@Nullable String phone) {
    this.phone = phone;
  }

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  @Nullable
  public LocalDate getPasswordDate() {
    return passwordDate;
  }

  public void setPasswordDate(@Nullable LocalDate passwordDate) {
    this.passwordDate = passwordDate;
  }
}
