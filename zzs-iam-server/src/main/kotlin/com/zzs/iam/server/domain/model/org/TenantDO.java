package com.zzs.iam.server.domain.model.org;

import cn.idealframework2.lang.StringUtils;
import com.zzs.iam.common.pojo.Tenant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_tenant")
public class TenantDO {
  public static final String ROUTER_SEPARATOR = "-";

  @Id
  private long id;

  /** 父租户id */
  @Indexed
  private long parentId = -1;

  @Nonnull
  @Indexed
  private String parentRouter = "";

  /** 平台编码 */
  @Nonnull
  @Indexed
  private String platform = "";

  /** 租户名称 */
  @Nonnull
  private String name = "";

  /** 地址 */
  @Nonnull
  private String address = "";

  /** 备注信息 */
  @Nonnull
  private String note = "";


  /** 是否已被冻结 */
  private boolean frozen = false;

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static TenantDO create(@Nonnull String platform,
                                @Nullable TenantDO parent,
                                @Nonnull String name,
                                @Nullable String address,
                                @Nullable String note) {
    TenantDO tenantDo = new TenantDO();
    if (parent != null) {
      tenantDo.setParentId(parent.getId());
      tenantDo.setParentRouter(parent.generateRouter());
    }
    tenantDo.setPlatform(platform);
    tenantDo.setName(name);
    tenantDo.setAddress(address);
    tenantDo.setNote(note);
    return tenantDo;
  }

  public void freeze() {
    this.setFrozen(true);
  }

  public void unfreeze() {
    this.setFrozen(false);
  }

  @Nonnull
  public Tenant toTenant() {
    Tenant tenant = new Tenant();
    tenant.setId(getId());
    tenant.setParentId(getParentId());
    tenant.setPlatform(getPlatform());
    tenant.setName(getName());
    tenant.setAddress(getAddress());
    tenant.setNote(getNote());
    tenant.setFrozen(isFrozen());
    tenant.setCreatedTime(getCreatedTime());
    tenant.setUpdatedTime(getUpdatedTime());
    return tenant;
  }

  @Nonnull
  public String generateRouter() {
    String parentRouter = this.getParentRouter();
    long id = this.getId();
    String idStr = String.valueOf(id);
    if (StringUtils.isBlank(parentRouter)) {
      return idStr + ROUTER_SEPARATOR;
    } else {
      return parentRouter + idStr + ROUTER_SEPARATOR;
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  @Nonnull
  public String getParentRouter() {
    return parentRouter;
  }

  public void setParentRouter(@Nonnull String parentRouter) {
    this.parentRouter = parentRouter;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  @Nonnull
  public String getAddress() {
    return address;
  }

  public void setAddress(@Nullable String address) {
    if (StringUtils.isBlank(address)) {
      address = "";
    }
    this.address = address;
  }

  @Nonnull
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    if (StringUtils.isBlank(note)) {
      note = "";
    }
    this.note = note;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
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
