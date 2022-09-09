package com.zzs.iam.common.pojo;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class Platform {

  /** 平台编码 */
  @Nonnull
  private String code = "";

  /** 平台名称 */
  private String name = "";

  private boolean multiTenant;

  /** 是否开启api级别的接口鉴权 */
  private boolean enableApiAuthenticate = true;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  @Nonnull
  public String getCode() {
    return code;
  }

  public void setCode(@Nonnull String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isMultiTenant() {
    return multiTenant;
  }

  public void setMultiTenant(boolean multiTenant) {
    this.multiTenant = multiTenant;
  }

  public boolean isEnableApiAuthenticate() {
    return enableApiAuthenticate;
  }

  public void setEnableApiAuthenticate(boolean enableApiAuthenticate) {
    this.enableApiAuthenticate = enableApiAuthenticate;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }

  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  public void setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
  }
}
