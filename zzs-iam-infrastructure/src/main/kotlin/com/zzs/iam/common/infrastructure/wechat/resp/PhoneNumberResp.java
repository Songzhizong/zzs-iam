package com.zzs.iam.common.infrastructure.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/1/11
 */
public class PhoneNumberResp {

  @Nullable
  @JsonProperty("errcode")
  private String errCode;

  @Nullable
  @JsonProperty("errmsg")
  private String errMsg;

  @Nullable
  @JsonProperty("phone_info")
  private PhoneInfo phoneInfo;

  @Nullable
  public String getErrCode() {
    return errCode;
  }

  public PhoneNumberResp setErrCode(@Nullable String errCode) {
    this.errCode = errCode;
    return this;
  }

  @Nullable
  public String getErrMsg() {
    return errMsg;
  }

  public PhoneNumberResp setErrMsg(@Nullable String errMsg) {
    this.errMsg = errMsg;
    return this;
  }

  @Nullable
  public PhoneInfo getPhoneInfo() {
    return phoneInfo;
  }

  public PhoneNumberResp setPhoneInfo(@Nullable PhoneInfo phoneInfo) {
    this.phoneInfo = phoneInfo;
    return this;
  }
}
