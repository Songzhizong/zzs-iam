package com.zzs.iam.common.pojo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class AuthClient {

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 是否多租户平台 0否 1是 */
  private boolean multiTenant;

  @Nonnull
  private String clientId = "";

  @Nonnull
  private String name = "";

  @Nullable
  private String note;

  /** access token有效期, 单位秒 */
  private int accessTokenValidity = 3600;

  /** refresh token有效期, 单位秒 */
  private int refreshTokenValidity = 1296000;

  /** token是否自动续期 */
  private boolean accessTokenAutoRenewal = true;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

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
  public String getClientId() {
    return clientId;
  }

  public void setClientId(@Nonnull String clientId) {
    this.clientId = clientId;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  @Nullable
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    this.note = note;
  }

  public int getAccessTokenValidity() {
    return accessTokenValidity;
  }

  public void setAccessTokenValidity(int accessTokenValidity) {
    this.accessTokenValidity = accessTokenValidity;
  }

  public int getRefreshTokenValidity() {
    return refreshTokenValidity;
  }

  public void setRefreshTokenValidity(int refreshTokenValidity) {
    this.refreshTokenValidity = refreshTokenValidity;
  }

  public boolean isAccessTokenAutoRenewal() {
    return accessTokenAutoRenewal;
  }

  public void setAccessTokenAutoRenewal(boolean accessTokenAutoRenewal) {
    this.accessTokenAutoRenewal = accessTokenAutoRenewal;
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
