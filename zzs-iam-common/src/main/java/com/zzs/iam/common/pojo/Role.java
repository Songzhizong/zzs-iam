package com.zzs.iam.common.pojo;

import com.zzs.iam.common.constants.RoleType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class Role {
  private long id;

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

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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
