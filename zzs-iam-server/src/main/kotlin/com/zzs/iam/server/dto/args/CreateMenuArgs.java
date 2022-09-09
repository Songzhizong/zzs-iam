package com.zzs.iam.server.dto.args;

import com.zzs.iam.common.constants.MenuType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class CreateMenuArgs {

  /**
   * 终端ID
   *
   * @required
   */
  @Nullable
  private String terminal;

  /** 父菜单ID */
  @Nullable
  private Long parentId;

  /**
   * 菜单名称
   *
   * @required
   */
  @Nullable
  private String name;

  /**
   * 菜单类型 MENU:菜单 BUTTON:按钮
   *
   * @required
   */
  @Nullable
  private MenuType type;

  /** 排序 */
  private int order;

  /** 图标 */
  @Nullable
  private String icon;

  /** 选中状态下的图标 */
  private String selectedIcon;

  /** 路由 */
  @Nullable
  private String url;

  /** 地址 */
  @Nullable
  private String path;

  /** 调用api列表 */
  @Nullable
  private List<String> apis;

  @Nullable
  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(@Nullable String terminal) {
    this.terminal = terminal;
  }

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
  public MenuType getType() {
    return type;
  }

  public void setType(@Nullable MenuType type) {
    this.type = type;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Nullable
  public String getIcon() {
    return icon;
  }

  public void setIcon(@Nullable String icon) {
    this.icon = icon;
  }

  public String getSelectedIcon() {
    return selectedIcon;
  }

  public void setSelectedIcon(String selectedIcon) {
    this.selectedIcon = selectedIcon;
  }

  @Nullable
  public String getUrl() {
    return url;
  }

  public void setUrl(@Nullable String url) {
    this.url = url;
  }

  @Nullable
  public String getPath() {
    return path;
  }

  public void setPath(@Nullable String path) {
    this.path = path;
  }

  @Nullable
  public List<String> getApis() {
    return apis;
  }

  public void setApis(@Nullable List<String> apis) {
    this.apis = apis;
  }
}
