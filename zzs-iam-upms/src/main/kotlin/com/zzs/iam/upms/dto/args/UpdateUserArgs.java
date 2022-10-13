package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class UpdateUserArgs {

  /** 姓名 */
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
  private String email;

  /** 头像 */
  @Nullable
  private String profilePhoto;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Nullable
  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(@Nullable String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }
}
