package com.zzs.iam.upms.dto.args;

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
  public List<String> getApis() {
    return apis;
  }

  public void setApis(@Nullable List<String> apis) {
    this.apis = apis;
  }
}
