package com.zzs.iam.common.event.tenant;

import com.zzs.framework.core.event.BaseEvent;
import com.zzs.iam.common.pojo.Tenant;
import com.zzs.iam.common.pojo.Tenant;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class TenantCreated extends BaseEvent {
  public static final String TOPIC = "iam.tenant.created";

  private Tenant tenant;

  @Nonnull
  @Override
  public String getTopic() {
    return TOPIC;
  }

  @Nonnull
  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(@Nonnull Tenant tenant) {
    this.tenant = tenant;
  }
}
