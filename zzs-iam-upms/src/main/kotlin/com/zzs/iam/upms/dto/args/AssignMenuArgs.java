package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/18
 */
public class AssignMenuArgs {
  /**
   * 角色id
   *
   * @required
   */
  @Nullable
  private Long roleId;

  /**
   * 终端编码
   */
  @Nullable
  private String terminal;

  /** 菜单列表 */
  @Nullable
  private List<Long> menus;

  @Nullable
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(@Nullable Long roleId) {
    this.roleId = roleId;
  }

  @Nullable
  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(@Nullable String terminal) {
    this.terminal = terminal;
  }

  @Nullable
  public List<Long> getMenus() {
    return menus;
  }

  public void setMenus(@Nullable List<Long> menus) {
    this.menus = menus;
  }
}
