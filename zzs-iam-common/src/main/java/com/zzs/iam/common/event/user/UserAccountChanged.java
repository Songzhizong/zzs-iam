package com.zzs.iam.common.event.user;

import com.zzs.framework.core.event.BaseEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用户账号变更事件
 *
 * @author 宋志宗 on 2022/8/19
 */
public class UserAccountChanged extends BaseEvent {
  public static final String TOPIC = "iam.user.account_changed";

  private long userId;

  @Nullable
  private String account;

  public UserAccountChanged() {
  }

  public UserAccountChanged(long userId, @Nullable String account) {
    this.userId = userId;
    this.account = account;
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
  public String getAccount() {
    return account;
  }

  public void setAccount(@Nullable String account) {
    this.account = account;
  }
}
