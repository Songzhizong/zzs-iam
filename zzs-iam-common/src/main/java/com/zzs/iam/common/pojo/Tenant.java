package com.zzs.iam.common.pojo;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class Tenant {
  private long id;

  /** 父租户id */
  private long parentId;

  /** 平台编码 */
  @Nonnull
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
  private boolean frozen;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

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

  public void setAddress(@Nonnull String address) {
    this.address = address;
  }

  @Nonnull
  public String getNote() {
    return note;
  }

  public void setNote(@Nonnull String note) {
    this.note = note;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
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
