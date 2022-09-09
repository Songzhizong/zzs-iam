package com.zzs.iam.common.event.user;

import com.zzs.framework.core.event.BaseEvent;
import com.zzs.iam.common.pojo.JoinedUser;
import com.zzs.iam.common.pojo.JoinedUser;

import javax.annotation.Nonnull;

/**
 * 平台用户冻结时间
 *
 * @author 宋志宗 on 2022/8/18
 */
public class PlatformUserFrozen extends BaseEvent {
  public static final String TOPIC = "iam.upm.platform_user.frozen";

  private String platform;

  private JoinedUser user;

  public PlatformUserFrozen() {
  }

  public PlatformUserFrozen(String platform, JoinedUser user) {
    this.platform = platform;
    this.user = user;
  }

  @Nonnull
  @Override
  public String getTopic() {
    return TOPIC;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public JoinedUser getUser() {
    return user;
  }

  public void setUser(JoinedUser user) {
    this.user = user;
  }
}
