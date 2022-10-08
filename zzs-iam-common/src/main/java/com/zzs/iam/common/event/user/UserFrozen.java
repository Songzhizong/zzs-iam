package com.zzs.iam.common.event.user;

import cn.idealframework2.event.BaseEvent;

import javax.annotation.Nonnull;

/**
 * 用户被冻结事件
 *
 * @author 宋志宗 on 2022/8/19
 */
public class UserFrozen extends BaseEvent {
  public static final String TOPIC = "iam.user.frozen";

  private long userId;

  public UserFrozen() {
  }

  public UserFrozen(long userId) {
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
