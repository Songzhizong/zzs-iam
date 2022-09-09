package com.zzs.iam.common.password;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2021/7/5
 */
public interface PasswordEncoder {

  /**
   * 对密码进行编码
   *
   * @param rawPassword 明文密码
   * @return 编码后的密码
   */
  @Nonnull
  String encode(@Nonnull CharSequence rawPassword);

  /**
   * 验证密码是否正确
   *
   * @param rawPassword     明文密码
   * @param encodedPassword 编码后的密码
   * @return 是否正确
   */
  boolean matches(@Nonnull CharSequence rawPassword, @Nonnull String encodedPassword);
}
