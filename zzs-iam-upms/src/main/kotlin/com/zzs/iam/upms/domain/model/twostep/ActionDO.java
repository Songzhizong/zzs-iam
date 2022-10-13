package com.zzs.iam.upms.domain.model.twostep;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 两步验证操作表
 *
 * @author 宋志宗 on 2022/8/25
 */
@Document("iam_twostep_action")
public class ActionDO {

  @Id
  private long id;

  @Nonnull
  @Indexed
  private String platform = "";

  /** 操作名称 */
  @Nonnull
  private String name = "";

  /** 接口列表 */
  @Nonnull
  private Set<String> apis = new HashSet<>();

  /** 是否开启 */
  private boolean enabled = true;

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  public long getId() {
    return id;
  }

  public ActionDO setId(long id) {
    this.id = id;
    return this;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public ActionDO setPlatform(@Nonnull String platform) {
    this.platform = platform;
    return this;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public ActionDO setName(@Nonnull String name) {
    this.name = name;
    return this;
  }

  @Nonnull
  public Set<String> getApis() {
    return apis;
  }

  public ActionDO setApis(@Nonnull Set<String> apis) {
    this.apis = apis;
    return this;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public ActionDO setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public long getVersion() {
    return version;
  }

  public ActionDO setVersion(long version) {
    this.version = version;
    return this;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public ActionDO setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
    return this;
  }

  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  public ActionDO setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
    return this;
  }
}
