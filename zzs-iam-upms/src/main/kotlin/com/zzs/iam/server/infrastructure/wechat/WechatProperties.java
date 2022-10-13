package com.zzs.iam.server.infrastructure.wechat;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/24
 */
public class WechatProperties {

  @Nonnull
  @NestedConfigurationProperty
  private MockWechatProperties mock = new MockWechatProperties();

  @Nonnull
  private String appId = "";

  @Nonnull
  private String appSecret = "";

  /** 获取微信accessToken的URL地址 */
  private String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

  /** 获取用户微信openId等信息的URL地址 */
  private String codeToSessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

  /** 获取用户手机号码的URL地址 */
  private String phoneNumberUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";

  @Nonnull
  public MockWechatProperties getMock() {
    return mock;
  }

  public void setMock(@Nonnull MockWechatProperties mock) {
    this.mock = mock;
  }

  @Nonnull
  public String getAppId() {
    return appId;
  }

  public void setAppId(@Nonnull String appId) {
    this.appId = appId;
  }

  @Nonnull
  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(@Nonnull String appSecret) {
    this.appSecret = appSecret;
  }

  public String getAccessTokenUrl() {
    return accessTokenUrl;
  }

  public void setAccessTokenUrl(String accessTokenUrl) {
    this.accessTokenUrl = accessTokenUrl;
  }

  public String getCodeToSessionUrl() {
    return codeToSessionUrl;
  }

  public void setCodeToSessionUrl(String codeToSessionUrl) {
    this.codeToSessionUrl = codeToSessionUrl;
  }

  public String getPhoneNumberUrl() {
    return phoneNumberUrl;
  }

  public void setPhoneNumberUrl(String phoneNumberUrl) {
    this.phoneNumberUrl = phoneNumberUrl;
  }
}
