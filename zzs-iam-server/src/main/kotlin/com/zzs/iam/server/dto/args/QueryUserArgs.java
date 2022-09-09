package com.zzs.iam.server.dto.args;

import com.zzs.framework.core.transmission.Paging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/17
 */
public class QueryUserArgs {
  @Nonnull
  private Paging paging = Paging.of(1, 10);

  /** 账号/邮箱/手机号 */
  @Nullable
  private String uniqueIdent;

  @Nonnull
  public Paging getPaging() {
    return paging;
  }

  public void setPaging(@Nonnull Paging paging) {
    this.paging = paging;
  }

  @Nullable
  public String getUniqueIdent() {
    return uniqueIdent;
  }

  public void setUniqueIdent(@Nullable String uniqueIdent) {
    this.uniqueIdent = uniqueIdent;
  }
}
