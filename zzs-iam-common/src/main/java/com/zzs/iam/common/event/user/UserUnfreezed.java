package com.zzs.iam.common.event.user;

import cn.idealframework2.event.BaseEvent;

import javax.annotation.Nonnull;

/**
 * 用户解冻事件
 *
 * @author 宋志宗 on 2022/8/19
 */
public class UserUnfreezed extends BaseEvent {
  public static final String TOPIC = "iam.user.unfreezed";

  private long userId;

  public UserUnfreezed() {
  }

  public UserUnfreezed(long userId) {
    this.userId = userId;
  }

  @Nonnull
  @Override
  public String getTopic() {
    return TOPIC;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }
}
