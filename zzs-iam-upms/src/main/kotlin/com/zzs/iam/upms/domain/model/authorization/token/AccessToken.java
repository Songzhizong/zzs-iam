package com.zzs.iam.upms.domain.model.authorization.token;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class AccessToken {

  @Nonnull
  @JsonProperty("token_type")
  private String tokenType = "";

  @Nonnull
  @JsonProperty("access_token")
  private String accessToken = "";

  @Nullable
  @JsonProperty("refresh_token")
  private String refreshToken = null;

  @JsonProperty("expires_in")
  private int expiresIn;

  @Nonnull
  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(@Nonnull String tokenType) {
    this.tokenType = tokenType;
  }

  @Nonnull
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(@Nonnull String accessToken) {
    this.accessToken = accessToken;
  }

  @Nullable
  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(@Nullable String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(int expiresIn) {
    this.expiresIn = expiresIn;
  }
}
