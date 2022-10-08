package com.zzs.iam.common.event.user;

import cn.idealframework2.event.BaseEvent;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/18
 */
public class UserLogined extends BaseEvent {
  public static final String TOPIC = "iam.user_logined";

  /** 平台 */
  @Nonnull
  private String platform = "";

  /** 用户ID */
  @Nonnull
  private String userId = "";

  @Nonnull
  @Override
  public String getTopic() {
    return TOPIC;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public void setUserId(@Nonnull String userId) {
    this.userId = userId;
  }
}
