package com.zzs.iam.upms.dto.args;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/20
 */
public class UserIdsArgs {

  /**
   * 需要移除的用户ID列表
   *
   * @required
   */
  @Nullable
  private List<String> userIds;

  @Nullable
  public List<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(@Nullable List<String> userIds) {
    this.userIds = userIds;
  }
}
