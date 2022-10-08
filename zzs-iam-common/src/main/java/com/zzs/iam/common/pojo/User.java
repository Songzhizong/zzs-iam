package com.zzs.iam.common.pojo;

import cn.idealframework2.utils.DesensitizeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class User {

  private long id;

  /** 姓名 */
  @Nonnull
  private String name = "";

  /** 昵称 */
  @Nullable
  private String nickname = "";

  /** 账号 */
  @Nullable
  private String account = "";

  /** 手机号码 */
  @Nullable
  private String phone = "";

  /** 邮箱 */
  @Nullable
  private String email = "";

  /** 头像 */
  @Nullable
  private String profilePhoto = "";


  /** 是否已被冻结 */
  private boolean frozen = false;

  /** 密码修改日期 */
  @Nullable
  private LocalDate passwordDate;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  /**
   * 信息脱敏
   *
   * @author 宋志宗 on 2022/6/7
   */
  @Nonnull
  public User desensitize() {
    setAccount(DesensitizeUtils.desensitize(getAccount(), 2, 2, '*'));
    setPhone(DesensitizeUtils.desensitizePhone(getPhone()));
    setEmail(DesensitizeUtils.desensitizeEmail(getEmail()));
    return this;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
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
  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(@Nullable String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  @Nullable
  public LocalDate getPasswordDate() {
    return passwordDate;
  }

  public void setPasswordDate(@Nullable LocalDate passwordDate) {
    this.passwordDate = passwordDate;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }

  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  public void setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
  }
}
