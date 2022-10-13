package com.zzs.iam.upms.domain.model.twostep;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 两步验证配置表
 *
 * @author 宋志宗 on 2022/8/25
 */
@Document("iam_twostep_cfg")
@CompoundIndexes({
  @CompoundIndex(name = "uk_platform_tenant", def = "{platform:1,tenant_id:1}"),
})
public class TwoStepCfgDO {

  @Id
  private long id;

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 租户ID */
  private long tenantId = -1;

  /** 是否启用两步验证 */
  private boolean enabled = true;

  /** 不启用两步验证的操作列表 */
  @Nonnull
  private Set<Long> disableActions = new HashSet<>();

  /** 两步验证的过期时间 */
  private int expireMinutes = 10;

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  public long getId() {
    return id;
  }

  public TwoStepCfgDO setId(long id) {
    this.id = id;
    return this;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public TwoStepCfgDO setPlatform(@Nonnull String platform) {
    this.platform = platform;
    return this;
  }

  public long getTenantId() {
    return tenantId;
  }

  public TwoStepCfgDO setTenantId(long tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public TwoStepCfgDO setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  @Nonnull
  public Set<Long> getDisableActions() {
    return disableActions;
  }

  public TwoStepCfgDO setDisableActions(@Nonnull Set<Long> disableActions) {
    this.disableActions = disableActions;
    return this;
  }

  public int getExpireMinutes() {
    return expireMinutes;
  }

  public TwoStepCfgDO setExpireMinutes(int expireMinutes) {
    this.expireMinutes = expireMinutes;
    return this;
  }

  public long getVersion() {
    return version;
  }

  public TwoStepCfgDO setVersion(long version) {
    this.version = version;
    return this;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public TwoStepCfgDO setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  public TwoStepCfgDO setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }
}
