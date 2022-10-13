package com.zzs.iam.upms.domain.model.role;

import cn.idealframework2.exception.BadRequestException;
import com.zzs.iam.common.constants.RoleType;
import com.zzs.iam.common.pojo.Role;
import com.zzs.iam.upms.domain.model.org.PlatformDO;
import com.zzs.iam.upms.domain.model.org.TenantDO;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_role")
@CompoundIndexes({
  @CompoundIndex(name = "uk_", def = "{platform:1,tenant_id:1,type:1,flag:1}", unique = true),
})
public class RoleDO {

  @Id
  private long id;

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 租户ID */
  private long tenantId = -1;

  @Nonnull
  private RoleType type = RoleType.GENERAL;

  /** 角色名称 */
  @Nonnull
  private String name = "";

  /** 备注信息 */
  @Nullable
  private String note;

  /** 是否基础角色 */
  private boolean basic = false;

  /**
   * 角色标记, 防止一个租户重复创建超管角色
   * 超管为 admin 普通角色为UUID
   */
  @Nonnull
  private String flag = "admin";

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static RoleDO create(@Nonnull PlatformDO platform,
                              @Nullable TenantDO tenant,
                              @Nonnull RoleType type,
                              @Nonnull String name,
                              @Nullable String note) {
    RoleDO roleDo = new RoleDO();
    roleDo.setPlatform(platform.getCode());
    if (tenant != null) {
      roleDo.setTenantId(tenant.getId());
    }
    roleDo.setType(type);
    roleDo.setName(name);
    roleDo.setNote(note);
    if (type == RoleType.GENERAL) {
      roleDo.setFlag(UUID.randomUUID().toString().replace("-", ""));
    }
    return roleDo;
  }

  public void cancelBasic() {
    if (!isBasic()) {
      return;
    }
    setBasic(true);
  }

  public void setAsBasic() {
    if (isBasic()) {
      return;
    }
    if (type == RoleType.ADMIN) {
      throw new BadRequestException("无法将超管设为默认角色");
    }
    setBasic(true);
  }

  @Nonnull
  public Role toRole() {
    Role role = new Role();
    role.setId(getId());
    role.setType(getType());
    role.setName(getName());
    role.setNote(getNote());
    role.setBasic(isBasic());
    role.setCreatedTime(getCreatedTime());
    role.setUpdatedTime(getUpdatedTime());
    return role;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  public long getTenantId() {
    return tenantId;
  }

  public void setTenantId(long tenantId) {
    this.tenantId = tenantId;
  }

  @Nonnull
  public RoleType getType() {
    return type;
  }

  public void setType(@Nonnull RoleType type) {
    this.type = type;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  @Nullable
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    this.note = note;
  }

  public boolean isBasic() {
    return basic;
  }

  public void setBasic(boolean basic) {
    this.basic = basic;
  }

  @Nonnull
  public String getFlag() {
    return flag;
  }

  public void setFlag(@Nonnull String flag) {
    this.flag = flag;
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
