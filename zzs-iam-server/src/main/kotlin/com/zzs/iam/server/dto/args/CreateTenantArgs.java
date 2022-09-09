package com.zzs.iam.server.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class CreateTenantArgs {

  /** 父租户id */
  @Nullable
  private Long parentId;

  /**
   * 租户名称
   *
   * @required
   */
  @Nullable
  private String name;

  /** 地址 */
  @Nullable
  private String address;

  /** 备注 */
  @Nullable
  private String note;

  @Nullable
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(@Nullable Long parentId) {
    this.parentId = parentId;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  public String getAddress() {
    return address;
  }

  public void setAddress(@Nullable String address) {
    this.address = address;
  }

  @Nullable
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    this.note = note;
  }
}
