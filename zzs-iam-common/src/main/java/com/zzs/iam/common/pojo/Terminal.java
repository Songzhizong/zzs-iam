package com.zzs.iam.common.pojo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * 终端信息
 *
 * @author 宋志宗 on 2022/8/16
 */
public class Terminal {

  /** 终端编码 */
  @Nonnull
  private String code = "";

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 终端名称 */
  @Nonnull
  private String name = "";

  /** 备注 */
  @Nullable
  private String note;

  private LocalDateTime createdTime;

  private LocalDateTime updatedTime;

  @Nonnull
  public String getCode() {
    return code;
  }

  public void setCode(@Nonnull String code) {
    this.code = code;
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

  @Nullable
  public String getNote() {
    return note;
  }

  public void setNote(@Nullable String note) {
    this.note = note;
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
