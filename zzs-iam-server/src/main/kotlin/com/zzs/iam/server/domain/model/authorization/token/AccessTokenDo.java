package com.zzs.iam.server.domain.model.authorization.token;

import com.zzs.framework.core.date.DateTimes;
import com.zzs.iam.server.domain.model.authorization.Authentication;
import com.zzs.iam.server.domain.model.authorization.BearerAuthorization;
import com.zzs.iam.server.domain.model.authorization.Authentication;
import com.zzs.iam.server.domain.model.authorization.BearerAuthorization;
import com.zzs.iam.server.domain.model.org.AuthClientDo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class AccessTokenDo {

  @Nonnull
  private String value = "";

  /** 是否自动续期 */
  private boolean autoRenewal;

  /** 有效时长, 单位秒 */
  private int validity;

  /** 过期时间 */
  private LocalDateTime expiration;

  /** 客户端id */
  @Nonnull
  private String clientId = "";

  @Nullable
  private RefreshTokenDo refreshToken;

  private Authentication authentication;

  @Nonnull
  public static AccessTokenDo create(boolean rememberMe,
                                     @Nonnull AuthClientDo authClient,
                                     @Nonnull Authentication authentication) {
    AccessTokenDo accessTokenDo = new AccessTokenDo();
    accessTokenDo.setValue(UUID.randomUUID().toString().replace("-", ""));
    accessTokenDo.setAutoRenewal(accessTokenDo.isAutoRenewal());
    int accessTokenValidity = authClient.getAccessTokenValidity();
    accessTokenDo.setValidity(accessTokenValidity);
    accessTokenDo.setExpiration(DateTimes.now().plusSeconds(accessTokenValidity));
    accessTokenDo.setClientId(authClient.getClientId());
    if (rememberMe) {
      int refreshTokenValidity = authClient.getRefreshTokenValidity();
      RefreshTokenDo refreshToken = RefreshTokenDo.create(refreshTokenValidity, authentication);
      accessTokenDo.setRefreshToken(refreshToken);
    }
    accessTokenDo.setAuthentication(authentication);
    return accessTokenDo;
  }

  @Transient
  public int getExpiresIn() {
    return expiration == null ? 0
      : (int) ((DateTimes.getTimestamp(expiration) - System.currentTimeMillis()) / 1000);
  }

  @Nonnull
  public AccessToken toAccessToken() {
    AccessToken accessToken = new AccessToken();
    accessToken.setTokenType(BearerAuthorization.TYPE);
    accessToken.setAccessToken(value);
    if (refreshToken != null) {
      accessToken.setRefreshToken(refreshToken.getValue());
    }
    accessToken.setExpiresIn(Math.toIntExact(validity));
    return accessToken;
  }

  @Nonnull
  public String getValue() {
    return value;
  }

  public void setValue(@Nonnull String value) {
    this.value = value;
  }

  public boolean isAutoRenewal() {
    return autoRenewal;
  }

  public void setAutoRenewal(boolean autoRenewal) {
    this.autoRenewal = autoRenewal;
  }

  public int getValidity() {
    return validity;
  }

  public void setValidity(int validity) {
    this.validity = validity;
  }

  public LocalDateTime getExpiration() {
    return expiration;
  }

  public void setExpiration(LocalDateTime expiration) {
    this.expiration = expiration;
  }

  @Nonnull
  public String getClientId() {
    return clientId;
  }

  public void setClientId(@Nonnull String clientId) {
    this.clientId = clientId;
  }

  @Nullable
  public RefreshTokenDo getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(@Nullable RefreshTokenDo refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Nonnull
  public Authentication getAuthentication() {
    return authentication;
  }

  public void setAuthentication(@Nonnull Authentication authentication) {
    this.authentication = authentication;
  }
}
