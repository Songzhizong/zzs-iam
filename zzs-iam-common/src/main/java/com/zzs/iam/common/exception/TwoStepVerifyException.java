package com.zzs.iam.common.exception;

import com.zzs.framework.core.exception.ForbiddenException;

/**
 * @author 宋志宗 on 2022/8/25
 */
public class TwoStepVerifyException extends ForbiddenException {

  public TwoStepVerifyException() {
    super("iam.two_steps_verify", "未通过两步验证");
  }
}
