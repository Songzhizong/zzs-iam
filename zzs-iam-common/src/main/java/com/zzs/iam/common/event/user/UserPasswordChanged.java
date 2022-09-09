package com.zzs.iam.common.event.user;

import com.zzs.framework.core.event.BaseEvent;

import javax.annotation.Nonnull;

/**
 * 用户密码发生变更
 *
 * @author 宋志宗 on 2022/8/19
 */
public class UserPasswordChanged extends BaseEvent {
  public static final String TOPIC = "iam.user.password_changed";

  private long userId;

  public UserPasswordChanged() {
  }

  public UserPasswordChanged(long userId) {
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
