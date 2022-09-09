package com.zzs.iam.common.constants;

/**
 * 用户验证策略
 *
 * @author 宋志宗 on 2022/8/25
 */
public enum UserAuthStrategy {
  /** 短信验证码验证 */
  PHONE,
  /** 邮箱验证 */
  EMAIL,
  /** 虚拟MFA验证 */
  MFA,
}
