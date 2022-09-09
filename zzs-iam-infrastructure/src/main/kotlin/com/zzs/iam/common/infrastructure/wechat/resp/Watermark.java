package com.zzs.iam.common.infrastructure.wechat.resp;

/**
 * @author 宋志宗 on 2022/1/11
 */
public class Watermark {

  /** 小程序appid */
  private String appid;

  /** 用户获取手机号操作的时间戳 */
  private Long timestamp;

  public String getAppid() {
    return appid;
  }

  public Watermark setAppid(String appid) {
    this.appid = appid;
    return this;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public Watermark setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }
}
