package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class RoleArgs {

  /**
   * 名称
   *
   * @required
   */
  @Nullable
  private String name;

  /** 备注 */
  @Nullable
  private String note;

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  @Nullable
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    this.note = note;
  }
}
