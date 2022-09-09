package com.zzs.iam.common.event.user;

import com.zzs.framework.core.event.BaseEvent;
import com.zzs.iam.common.pojo.User;

import javax.annotation.Nonnull;

/**
 * 用户新增事件
 *
 * @author 宋志宗 on 2022/8/15
 */
public class UserCreated extends BaseEvent {
  public static final String TOPIC = "iam.user.created";

  private User user;

  @Nonnull
  @Override
  public String getTopic() {
    return TOPIC;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
