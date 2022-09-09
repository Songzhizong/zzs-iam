package com.zzs.iam.common.exception;

import com.zzs.framework.core.exception.ForbiddenException;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class NoTenantAccessException extends ForbiddenException {
  public NoTenantAccessException() {
    super("iam.no_tenant_access", "没有该租户的访问权限");
  }
}
