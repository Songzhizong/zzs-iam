package com.zzs.iam.server.domain.model.org;

import com.zzs.iam.common.pojo.AuthClient;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_auth_client")
public class AuthClientDO {

  @Id
  private long id;

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 是否多租户平台 0否 1是 */
  private boolean multiTenant;

  @Nonnull
  @Indexed(unique = true)
  private String clientId = "";

  @Nonnull
  private String clientSecret = "";

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

  /** 重复登录限制 */
  private int repetitionLoginLimit = 1;

  /** 用于登录请求的认证头 */
  @Nonnull
  private String tokenValue = "";

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static AuthClientDO create(@Nonnull PlatformDO platform,
                                    @Nonnull String clientId,
                                    @Nonnull String clientSecret,
                                    @Nonnull String name,
                                    @Nullable String note,
                                    @Nullable Integer accessTokenValidity,
                                    @Nullable Integer refreshTokenValidity,
                                    @Nullable Boolean accessTokenAutoRenewal,
                                    @Nullable Integer repetitionLoginLimit,
                                    @Nonnull String tokenValue) {
    AuthClientDO authClientDo = new AuthClientDO();
    authClientDo.setPlatform(platform.getCode());
    authClientDo.setMultiTenant(platform.isMultiTenant());
    authClientDo.setClientId(clientId);
    authClientDo.setClientSecret(clientSecret);
    authClientDo.setName(name);
    authClientDo.setNote(note);
    authClientDo.setAccessTokenValidity(accessTokenValidity);
    authClientDo.setRefreshTokenValidity(refreshTokenValidity);
    authClientDo.setAccessTokenAutoRenewal(accessTokenAutoRenewal);
    authClientDo.setRepetitionLoginLimit(repetitionLoginLimit);
    authClientDo.setTokenValue(tokenValue);
    return authClientDo;
  }

  @Nonnull
  public AuthClient toAuthClient() {
    AuthClient authClient = new AuthClient();
    authClient.setPlatform(getPlatform());
    authClient.setMultiTenant(isMultiTenant());
    authClient.setClientId(getClientId());
    authClient.setName(getName());
    authClient.setNote(getNote());
    authClient.setAccessTokenValidity(getAccessTokenValidity());
    authClient.setRefreshTokenValidity(getRefreshTokenValidity());
    authClient.setAccessTokenAutoRenewal(isAccessTokenAutoRenewal());
    authClient.setCreatedTime(getCreatedTime());
    authClient.setUpdatedTime(getUpdatedTime());
    return authClient;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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
  public String getClientId() {
    return clientId;
  }

  public void setClientId(@Nonnull String clientId) {
    this.clientId = clientId;
  }

  @Nonnull
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(@Nonnull String clientSecret) {
    this.clientSecret = clientSecret;
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

  public void setAccessTokenValidity(@Nullable Integer accessTokenValidity) {
    if (accessTokenValidity == null) {
      accessTokenValidity = 3600;
    }
    this.accessTokenValidity = Math.max(accessTokenValidity, 600);
  }

  public int getRefreshTokenValidity() {
    return refreshTokenValidity;
  }

  public void setRefreshTokenValidity(@Nullable Integer refreshTokenValidity) {
    if (refreshTokenValidity == null) {
      refreshTokenValidity = 1296000;
    }
    this.refreshTokenValidity = Math.max(refreshTokenValidity, 1800);
  }

  public boolean isAccessTokenAutoRenewal() {
    return accessTokenAutoRenewal;
  }

  public void setAccessTokenAutoRenewal(@Nullable Boolean accessTokenAutoRenewal) {
    if (accessTokenAutoRenewal == null) {
      accessTokenAutoRenewal = true;
    }
    this.accessTokenAutoRenewal = accessTokenAutoRenewal;
  }

  public int getRepetitionLoginLimit() {
    return repetitionLoginLimit;
  }

  public void setRepetitionLoginLimit(@Nullable Integer repetitionLoginLimit) {
    if (repetitionLoginLimit == null) {
      repetitionLoginLimit = 1;
    }
    this.repetitionLoginLimit = repetitionLoginLimit;
  }

  @Nonnull
  public String getTokenValue() {
    return tokenValue;
  }

  public void setTokenValue(@Nonnull String tokenValue) {
    this.tokenValue = tokenValue;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
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
