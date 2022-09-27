package com.zzs.iam.server.infrastructure.security;

import com.zzs.framework.core.exception.ForbiddenException;
import com.zzs.iam.common.exception.MissTenantIdException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class SecurityContext {

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  private boolean multiTenant;

  /** 用户ID */
  @Nonnull
  private String userId = "";

  @Nullable
  private Long tenantId = null;

  /**
   * 非多租户平台返回null, 多租户平台返回租户id
   *
   * @author 宋志宗 on 2022/8/16
   */
  @Nullable
  public Long possibleTenantId() throws ForbiddenException {
    if (!multiTenant) {
      return null;
    }
    if (tenantId == null) {
      throw new MissTenantIdException();
    }
    return tenantId;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  public boolean isMultiTenant() {
    return multiTenant;
  }

  public void setMultiTenant(boolean multiTenant) {
    this.multiTenant = multiTenant;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public void setUserId(@Nonnull String userId) {
    this.userId = userId;
  }

  @Nullable
  public Long getTenantId() {
    return tenantId;
  }

  public void setTenantId(@Nullable Long tenantId) {
    this.tenantId = tenantId;
  }
}
