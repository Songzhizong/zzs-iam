package com.zzs.iam.server.domain.model.front;

import com.zzs.iam.common.pojo.Terminal;
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
 * 终端信息
 *
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_terminal")
public class TerminalDO {

  @Id
  private long id;

  /** 终端编码 */
  @Nonnull
  @Indexed(unique = true)
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

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static TerminalDO create(@Nonnull String code,
                                  @Nonnull String platform,
                                  @Nonnull String name,
                                  @Nullable String note) {
    TerminalDO terminalDo = new TerminalDO();
    terminalDo.setCode(code);
    terminalDo.setPlatform(platform);
    terminalDo.setName(name);
    terminalDo.setNote(note);
    return terminalDo;
  }

  @Nonnull
  public Terminal toTerminal() {
    Terminal terminal = new Terminal();
    terminal.setCode(getCode());
    terminal.setPlatform(getPlatform());
    terminal.setName(getName());
    terminal.setNote(getNote());
    terminal.setCreatedTime(getCreatedTime());
    terminal.setUpdatedTime(getUpdatedTime());
    return terminal;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

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
