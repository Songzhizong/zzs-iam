package com.zzs.iam.server.infrastructure.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/1/11
 */
public class AccessTokenResp {
  @Nullable
  @JsonProperty("errcode")
  private String errCode;

  @Nullable
  @JsonProperty("errmsg")
  private String errMsg;

  @Nullable
  @JsonProperty("access_token")
  private String accessToken;

  @Nullable
  @JsonProperty("expires_in")
  private Long expiresIn;

  @Nullable
  public String getErrCode() {
    return errCode;
  }

  public AccessTokenResp setErrCode(@Nullable String errCode) {
    this.errCode = errCode;
    return this;
  }

  @Nullable
  public String getErrMsg() {
    return errMsg;
  }

  public AccessTokenResp setErrMsg(@Nullable String errMsg) {
    this.errMsg = errMsg;
    return this;
  }

  @Nullable
  public String getAccessToken() {
    return accessToken;
  }

  public AccessTokenResp setAccessToken(@Nullable String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  @Nullable
  public Long getExpiresIn() {
    return expiresIn;
  }

  public AccessTokenResp setExpiresIn(@Nullable Long expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }
}
