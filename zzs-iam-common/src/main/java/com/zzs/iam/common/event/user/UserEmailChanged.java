package com.zzs.iam.common.event.user;

import com.zzs.framework.core.event.BaseEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用户邮箱变更事件
 *
 * @author 宋志宗 on 2022/8/19
 */
public class UserEmailChanged extends BaseEvent {
  public static final String TOPIC = "iam.user.email_changed";

  private long userId;

  @Nullable
  private String email;

  public UserEmailChanged() {
  }

  public UserEmailChanged(long userId, @Nullable String email) {
    this.userId = userId;
    this.email = email;
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

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }
}
