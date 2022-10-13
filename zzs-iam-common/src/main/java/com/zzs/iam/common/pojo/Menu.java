package com.zzs.iam.common.pojo;

import com.zzs.iam.common.constants.MenuType;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * 菜单信息
 *
 * @author 宋志宗 on 2022/8/16
 */
public class Menu {

  private long id;

  /** 父菜单id */
  private long parentId = -1;

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 终端编码 */
  @Nonnull
  private String terminal = "";

  /** 菜单名称 */
  @Nonnull
  private String name = "";

  /** 菜单类型 */
  @Nonnull
  private MenuType type = MenuType.MENU;

  /** 排序 */
  private int order = 0;

  /** 调用api列表 */
  @Nonnull
  private Set<String> apis = Collections.emptySet();

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
  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(@Nonnull String terminal) {
    this.terminal = terminal;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  @Nonnull
  public MenuType getType() {
    return type;
  }

  public void setType(@Nonnull MenuType type) {
    this.type = type;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Nonnull
  public Set<String> getApis() {
    return apis;
  }

  public void setApis(@Nonnull Set<String> apis) {
    this.apis = apis;
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
