package com.zzs.iam.upms.dto.args;

import cn.idealframework2.transmission.Paging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class QueryTenantArgs {
  /** 分页参数 */
  @Nonnull
  private Paging paging = Paging.of(1, 10);

  /** 租户名称 */
  @Nullable
  private String name;

  @Nonnull
  public Paging getPaging() {
    return paging;
  }

  public void setPaging(@Nonnull Paging paging) {
    this.paging = paging;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

}
