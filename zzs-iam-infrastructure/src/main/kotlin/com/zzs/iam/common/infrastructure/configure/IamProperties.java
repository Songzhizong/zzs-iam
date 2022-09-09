package com.zzs.iam.common.infrastructure.configure;

import com.zzs.iam.common.infrastructure.wechat.WechatProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/15
 */
@ConfigurationProperties("zzs-iam")
public class IamProperties {

  /** 微信配置 */
  @Nonnull
  @NestedConfigurationProperty
  private WechatProperties wechat = new WechatProperties();

  @Nonnull
  public WechatProperties getWechat() {
    return wechat;
  }

  public void setWechat(@Nonnull WechatProperties wechat) {
    this.wechat = wechat;
  }
}
