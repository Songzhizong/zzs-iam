package com.zzs.iam.server.domain.model.org;

import cn.idealframework2.event.EventTuple;
import com.zzs.iam.common.pojo.Platform;
import com.zzs.iam.server.domain.model.org.event.builder.PlatformCreatedBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_platform")
public class PlatformDO {

  @Id
  private long id;

  /** 平台编码 */
  @Nonnull
  @Indexed(unique = true)
  private String code = "";

  /** 平台名称 */
  @Nonnull
  private String name = "";

  private boolean multiTenant;

  /** 租户超管是否拥有所有菜单的权限 */
  private boolean tenantHasAllMenus = false;

  /** 是否开启api级别的接口鉴权 */
  private boolean enableApiAuthenticate = true;

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static EventTuple<PlatformDO> create(@Nonnull String code,
                                              @Nonnull String name,
                                              boolean multiTenant,
                                              boolean tenantHasAllMenus,
                                              boolean enableApiAuthenticate) {
    PlatformDO platformDo = new PlatformDO();
    platformDo.setCode(code);
    platformDo.setName(name);
    platformDo.setMultiTenant(multiTenant);
    if (multiTenant) {
      platformDo.setTenantHasAllMenus(tenantHasAllMenus);
    }
    platformDo.setEnableApiAuthenticate(enableApiAuthenticate);
    return EventTuple.of(platformDo, new PlatformCreatedBuilder(platformDo));
  }

  @Nonnull
  public Platform toPlatform() {
    Platform platform = new Platform();
    platform.setCode(getCode());
    platform.setName(getName());
    platform.setMultiTenant(isMultiTenant());
    platform.setEnableApiAuthenticate(isEnableApiAuthenticate());
    platform.setCreatedTime(getCreatedTime());
    platform.setUpdatedTime(getUpdatedTime());
    return platform;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Nonnull
  public String getCode() {
    return code;
  }

  public void setCode(@Nonnull String code) {
    this.code = code;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
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

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
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
