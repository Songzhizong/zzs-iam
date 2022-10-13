package com.zzs.iam.server.dto.resp;

/**
 * 用户在租户下的信息
 *
 * @author 宋志宗 on 2022/8/16
 */
public class UserTenantInfo {
  /** 租户id */
  private long tenantId;

  /** 租户名称 */
  private String tenantName;

  /** 是否被冻结 */
  private boolean frozen;

  public long getTenantId() {
    return tenantId;
  }

  public void setTenantId(long tenantId) {
    this.tenantId = tenantId;
  }

  public String getTenantName() {
    return tenantName;
  }

  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }
}
