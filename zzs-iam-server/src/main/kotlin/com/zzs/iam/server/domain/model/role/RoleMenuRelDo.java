package com.zzs.iam.server.domain.model.role;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/16
 */
@Document("iam_role_menu_rel")
@CompoundIndexes({
  @CompoundIndex(name = "uk_role_terminal_menu", def = "{role_id:1,terminal_id:1,menu_id:1}", unique = true),
})
public class RoleMenuRelDo {

  @Id
  private long id;

  /** 角色ID */
  private long roleId;

  /** 终端ID */
  private String terminal;

  /** 菜单ID */
  private long menuId;

  @Nonnull
  public static RoleMenuRelDo create(long roleId, String terminal, long menuId) {
    RoleMenuRelDo roleMenuRel = new RoleMenuRelDo();
    roleMenuRel.setRoleId(roleId);
    roleMenuRel.setTerminal(terminal);
    roleMenuRel.setMenuId(menuId);
    return roleMenuRel;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getRoleId() {
    return roleId;
  }

  public void setRoleId(long roleId) {
    this.roleId = roleId;
  }

  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(String terminal) {
    this.terminal = terminal;
  }

  public long getMenuId() {
    return menuId;
  }

  public void setMenuId(long menuId) {
    this.menuId = menuId;
  }
}
