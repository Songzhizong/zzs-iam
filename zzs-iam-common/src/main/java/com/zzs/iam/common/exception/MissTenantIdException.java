package com.zzs.iam.common.exception;

import com.zzs.framework.core.exception.ForbiddenException;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class MissTenantIdException extends ForbiddenException {
  public MissTenantIdException() {
    super("iam.miss_tenant_id", "请求未携带租户ID");
  }
}
