package com.zzs.iam.server.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class CreatePlatformArgs {

  /** 平台编码 */
  @Nullable
  private String code;

  /** 平台名称 */
  @Nullable
  private String name;

  /** 是否多租户平台 */
  private boolean multiTenant;

  /** 租户超管是否直接拥有这个平台所有的菜单权限 */
  private boolean tenantHasAllMenus;

  /** 是否开启api级别的接口鉴权 */
  private boolean enableApiAuthenticate;

  @Nullable
  public String getCode() {
    return code;
  }

  public void setCode(@Nullable String code) {
    this.code = code;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public boolean isMultiTenant() {
    return multiTenant;
  }

  public void setMultiTenant(boolean multiTenant) {
    this.multiTenant = multiTenant;
  }

  public boolean isTenantHasAllMenus() {
    return tenantHasAllMenus;
  }

  public void setTenantHasAllMenus(boolean tenantHasAllMenus) {
    this.tenantHasAllMenus = tenantHasAllMenus;
  }

  public boolean isEnableApiAuthenticate() {
    return enableApiAuthenticate;
  }

  public void setEnableApiAuthenticate(boolean enableApiAuthenticate) {
    this.enableApiAuthenticate = enableApiAuthenticate;
  }
}
