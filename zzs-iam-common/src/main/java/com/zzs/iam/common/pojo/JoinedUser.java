package com.zzs.iam.common.pojo;

import com.zzs.framework.core.utils.DesensitizeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * @author 宋志宗 on 2022/8/23
 */
public class JoinedUser {

  /** 用户id */
  @Nonnull
  private String userId = "";

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
  @Nullable
  private String email;

  /** 角色列表 */
  @Nullable
  private Set<Long> roles = Collections.emptySet();

  /** 是否已被冻结 */
  private boolean frozen = false;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  /**
   * 信息脱敏
   *
   * @author 宋志宗 on 2022/6/7
   */
  @Nonnull
  public JoinedUser desensitize() {
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
  public Set<Long> getRoles() {
    return roles;
  }

  public void setRoles(@Nullable Set<Long> roles) {
    this.roles = roles;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
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
