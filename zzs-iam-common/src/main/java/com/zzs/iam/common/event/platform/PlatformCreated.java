package com.zzs.iam.common.event.platform;

import com.zzs.framework.core.event.BaseEvent;
import com.zzs.iam.common.pojo.Platform;
import com.zzs.iam.common.pojo.Platform;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class PlatformCreated extends BaseEvent {
  public static final String TOPIC = "iam.platform.created";

  private Platform platform;

  @Nonnull
  @Override
  public String getTopic() {
    return TOPIC;
  }

  @Nonnull
  public Platform getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull Platform platform) {
    this.platform = platform;
  }
}
