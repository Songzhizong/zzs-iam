package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class CreateAuthClientArgs {
  /** 平台编码 */
  @Nullable
  private String platform;

  /** 客户端ID */
  @Nullable
  private String clientId;

  /** 客户端密钥 */
  @Nullable
  private String clientSecret;

  /** 客户端名称 */
  @Nullable
  private String name;

  /** 备注 */
  @Nullable
  private String note;

  /** access token有效期, 单位秒 */
  @Nullable
  private Integer accessTokenValidity;

  /** refresh token有效期, 单位秒 */
  @Nullable
  private Integer refreshTokenValidity;

  /** token是否自动续期 */
  @Nullable
  private Boolean accessTokenAutoRenewal;

  /** 重复登录次数限制 */
  @Nullable
  private Integer repetitionLoginLimit;

  @Nullable
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nullable String platform) {
    this.platform = platform;
  }

  @Nullable
  public String getClientId() {
    return clientId;
  }

  public void setClientId(@Nullable String clientId) {
    this.clientId = clientId;
  }

  @Nullable
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(@Nullable String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    this.note = note;
  }

  @Nullable
  public Integer getAccessTokenValidity() {
    return accessTokenValidity;
  }

  public void setAccessTokenValidity(@Nullable Integer accessTokenValidity) {
    this.accessTokenValidity = accessTokenValidity;
  }

  @Nullable
  public Integer getRefreshTokenValidity() {
    return refreshTokenValidity;
  }

  public void setRefreshTokenValidity(@Nullable Integer refreshTokenValidity) {
    this.refreshTokenValidity = refreshTokenValidity;
  }

  @Nullable
  public Boolean getAccessTokenAutoRenewal() {
    return accessTokenAutoRenewal;
  }

  public void setAccessTokenAutoRenewal(@Nullable Boolean accessTokenAutoRenewal) {
    this.accessTokenAutoRenewal = accessTokenAutoRenewal;
  }

  @Nullable
  public Integer getRepetitionLoginLimit() {
    return repetitionLoginLimit;
  }

  public void setRepetitionLoginLimit(@Nullable Integer repetitionLoginLimit) {
    this.repetitionLoginLimit = repetitionLoginLimit;
  }
}
