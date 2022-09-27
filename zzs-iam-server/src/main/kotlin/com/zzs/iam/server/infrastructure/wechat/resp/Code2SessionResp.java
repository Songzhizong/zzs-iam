package com.zzs.iam.server.infrastructure.wechat.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.Transient;

/**
 * @author 宋志宗 on 2021/11/10
 */
public class Code2SessionResp {
  private String rid;
  /** 用户唯一标识 */
  @JsonProperty("openid")
  private String openId;

  /** 会话密钥 */
  @JsonProperty("session_key")
  private String sessionKey;

  /** 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回，详见 UnionID 机制说明。 */
  @JsonProperty("unionid")
  private String unionId;

  /** 错误码 */
  @JsonProperty("errcode")
  private int errCode = 0;

  /** 错误信息 */
  @JsonProperty("errmsg")
  private String errMsg;

  @Transient
  public boolean isSuccess() {
    return errCode == 0;
  }

  @Transient
  public boolean isFailure() {
    return !isSuccess();
  }

  public String getRid() {
    return rid;
  }

  public Code2SessionResp setRid(String rid) {
    this.rid = rid;
    return this;
  }

  public String getOpenId() {
    return openId;
  }

  public Code2SessionResp setOpenId(String openId) {
    this.openId = openId;
    return this;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public Code2SessionResp setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
    return this;
  }

  public String getUnionId() {
    return unionId;
  }

  public Code2SessionResp setUnionId(String unionId) {
    this.unionId = unionId;
    return this;
  }

  public int getErrCode() {
    return errCode;
  }

  public Code2SessionResp setErrCode(int errCode) {
    this.errCode = errCode;
    return this;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public Code2SessionResp setErrMsg(String errMsg) {
    this.errMsg = errMsg;
    return this;
  }
}
