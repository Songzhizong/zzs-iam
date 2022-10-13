package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class CreateTerminalArgs {

  /**
   * 平台编码
   *
   * @required
   */
  @Nullable
  private String platform;

  /**
   * 终端编码
   *
   * @required
   */
  @Nullable
  private String code;
  /**
   * 终端名称
   *
   * @required
   */
  @Nullable
  private String name;

  /** 备注 */
  @Nullable
  private String note;

  @Nullable
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nullable String platform) {
    this.platform = platform;
  }

  @Nullable
  public String getCode() {
    return code;
  }

  public void setCode(@Nullable String code) {
    this.code = code;
  }

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
