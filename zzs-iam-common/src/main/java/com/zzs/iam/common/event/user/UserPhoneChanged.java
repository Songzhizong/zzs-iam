package com.zzs.iam.common.event.user;

import cn.idealframework2.event.BaseEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用户手机号变更事件
 *
 * @author 宋志宗 on 2022/8/19
 */
public class UserPhoneChanged extends BaseEvent {
  public static final String TOPIC = "iam.user.phone_changed";

  private long userId;

  @Nullable
  private String phone;

  public UserPhoneChanged() {
  }

  public UserPhoneChanged(long userId, @Nullable String phone) {
    this.userId = userId;
    this.phone = phone;
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
  public String getPhone() {
    return phone;
  }

  public void setPhone(@Nullable String phone) {
    this.phone = phone;
  }
}
