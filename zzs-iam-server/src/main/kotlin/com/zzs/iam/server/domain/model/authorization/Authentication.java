package com.zzs.iam.server.domain.model.authorization;

import com.zzs.framework.core.date.DateTimes;
import com.zzs.iam.server.domain.model.org.AuthClientDO;
import com.zzs.iam.server.domain.model.user.AuthUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class Authentication {
  /** 所在平台 */
  @Nonnull
  private String platform = "";

  /** 是否为多租户平台 */
  private boolean multiTenant;

  /** 用户ID */
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

  /** 密码过期时间 */
  private long passwordExpire = Long.MAX_VALUE;

  @Nonnull
  public static Authentication crete(@Nonnull AuthClientDO client,
                                     @Nonnull AuthUser user,
                                     int passwordExpireDays) {
    Authentication authentication = new Authentication();
    authentication.setPlatform(client.getPlatform());
    authentication.setMultiTenant(client.isMultiTenant());
    authentication.setUserId(user.getUserId());
    authentication.setName(user.getName());
    authentication.setNickname(user.getNickname());
    authentication.setAccount(user.getAccount());
    authentication.setPhone(user.getPhone());
    authentication.setEmail(user.getEmail());
    LocalDate passwordDate = user.getPasswordDate();
    if (passwordExpireDays > 0 && passwordDate != null) {
      long epochSecond = passwordDate.plusDays(passwordExpireDays)
        .toEpochSecond(LocalTime.MAX, DateTimes.getOffset());
      authentication.setPasswordExpire(epochSecond * 1000);
    }
    return authentication;
  }

  @Nonnull
  public AuthUser toUser() {
    AuthUser upmsUser = new AuthUser();
    upmsUser.setUserId(getUserId());
    upmsUser.setName(getName());
    upmsUser.setNickname(getNickname());
    upmsUser.setAccount(getAccount());
    upmsUser.setPhone(getPhone());
    upmsUser.setEmail(getEmail());
    upmsUser.setPasswordDate(getPasswordDate());
    return upmsUser;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  public boolean isMultiTenant() {
    return multiTenant;
  }

  public void setMultiTenant(boolean multiTenant) {
    this.multiTenant = multiTenant;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public void setUserId(@Nonnull String userId) {
    this.userId = userId;
  }

  public boolean isPasswordExpired() {
    return System.currentTimeMillis() > passwordExpire;
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

  public long getPasswordExpire() {
    return passwordExpire;
  }

  public void setPasswordExpire(long passwordExpire) {
    this.passwordExpire = passwordExpire;
  }

  public void setPasswordDate(@Nullable LocalDate passwordDate) {
    this.passwordDate = passwordDate;
  }

  public static void main(String[] args) {
    LocalTime now = LocalTime.now();
    long currentTimeMillis = System.currentTimeMillis();
    long l = LocalDate.now().toEpochSecond(now, DateTimes.getOffset()) * 1000;
    System.out.println(currentTimeMillis - l);
  }
}
