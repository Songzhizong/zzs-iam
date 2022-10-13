package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/18
 */
public class ChangeUserRoleArgs {

  /**
   * 用户ID
   *
   * @required
   */
  @Nullable
  private List<String> userIds;

  /** 为空代表移出所有角色 */
  @Nullable
  private List<Long> roles;

  @Nullable
  public List<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(@Nullable List<String> userIds) {
    this.userIds = userIds;
  }

  @Nullable
  public List<Long> getRoles() {
    return roles;
  }

  public void setRoles(@Nullable List<Long> roles) {
    this.roles = roles;
  }
}
