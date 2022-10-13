package com.zzs.iam.common.pojo;

import com.zzs.iam.common.constants.MenuType;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/17
 */
public class SimpleMenu {
  private long id;


  /** 父菜单id */
  private long parentId;

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
}
