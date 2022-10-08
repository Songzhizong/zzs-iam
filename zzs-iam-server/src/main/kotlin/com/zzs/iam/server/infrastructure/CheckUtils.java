/*
 * Copyright 2021 cn.idealframework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zzs.iam.server.infrastructure;

import cn.idealframework2.exception.BadRequestException;
import cn.idealframework2.lang.StringUtils;
import cn.idealframework2.utils.Asserts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 宋志宗 on 2021/7/3
 */
public final class CheckUtils {
  /** 手机号, 1开头的11位数字 */
  private static final String MOBILE_REGEX = "^(1)\\d{10}$";
  private static final Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_REGEX);

  /** 账号 以英文字母开头, 长度 >=6  && <=64 */
  private static final String ACCOUNT_REGEX = "^[a-zA-Z]\\w{5,63}$";
  private static final Pattern ACCOUNT_PATTERN = Pattern.compile(ACCOUNT_REGEX);

  /** 邮箱 */
  private static final String EMAIL_REGEX
    = "^[a-zA-Z\\d_.-]+@[a-zA-Z\\d-]+(\\.[a-zA-Z\\d-]+)*\\.[a-zA-Z\\d]{2,6}$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

  /** 身份证 */
  private static final String ID_CARD_REGEX
    = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)"
    + "|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
  private static final Pattern ID_CARD_PATTERN = Pattern.compile(ID_CARD_REGEX);

  private static final String PASSWORD_REGEX = "^(?![a-zA-Z]+$)(?![A-Z\\d]+$)(?![A-Z\\W_]+$)(?![a-z\\d]+$)(?![a-z\\W_]+$)(?![\\d\\W_]+$)[a-zA-Z\\d\\W_]{8,256}$";
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

  /**
   * 校验手机号是否合法
   *
   * @param mobile 手机号
   * @return 是否合法
   */
  public static boolean checkMobile(@Nullable String mobile) {
    return StringUtils.isNotBlank(mobile) && MOBILE_PATTERN.matcher(mobile).matches();
  }

  /**
   * 校验手机号是否合法
   *
   * @param mobile 手机号
   * @throws IllegalArgumentException 非法手机号抛出异常
   */
  public static void checkMobile(@Nullable String mobile, @Nonnull String message) {
    Asserts.assertTrue(checkMobile(mobile), message);
  }


  /**
   * 校验用户账号是否合法
   *
   * @param account 用户账号
   * @return 是否合法
   */
  public static boolean checkAccount(@Nullable String account) {
    return StringUtils.isNotBlank(account) && ACCOUNT_PATTERN.matcher(account).matches();
  }

  /**
   * 校验用户账号是否合法
   *
   * @param account 用户账号
   * @throws IllegalArgumentException 非法用户账号抛出异常
   */
  public static void checkAccount(@Nullable String account, @Nonnull String message) {
    Asserts.assertTrue(checkAccount(account), message);
  }

  /**
   * 校验邮箱是否合法
   *
   * @param email 邮箱
   * @return 是否合法
   */
  public static boolean checkEmail(@Nullable String email) {
    return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
  }

  /**
   * 校验邮箱是否合法
   *
   * @param email 邮箱
   * @throws IllegalArgumentException 非法邮箱抛出异常
   */
  public static void checkEmail(@Nullable String email, @Nonnull String message) {
    Asserts.assertTrue(checkEmail(email), message);
  }

  /**
   * 校验身份证号码是否合法
   *
   * @param idCard 身份证号码
   * @return 是否合法
   */
  public static boolean checkIdCard(@Nullable String idCard) {
    return StringUtils.isNotBlank(idCard) && ID_CARD_PATTERN.matcher(idCard).matches();
  }

  /**
   * 校验身份证号码是否合法
   *
   * @param idCard 身份证号码
   * @throws IllegalArgumentException 非法身份证号码抛出异常
   */
  public static void checkIdCard(@Nullable String idCard, @Nonnull String message) {
    Asserts.assertTrue(checkIdCard(idCard), message);
  }

  /**
   * 验证密码：8-25位，必须包含英文大小写、数字、特殊字符(~!@#$%^&*?)中的三种
   * 密码与用户名无关性,密码中不得出现用户名的完整字符串、大小写变为或形似变换测字符串(0 -> o/O, l -> I)
   *
   * @param password 密码
   * @param account  账号
   * @author 宋志宗 on 2018-12-21 13:58
   */
  public static void checkPassword(@Nullable String password, @Nullable String account) {
    int minLength = 8;
    int maxLength = 256;
    Asserts.notBlank(password, "密码不能为空");
    int length = password.length();
    if (length < minLength || length > maxLength) {
      throw new BadRequestException("密码长度应为8~256");
    }
    if (StringUtils.isNotBlank(account)) {
      String upperCaseCasePassword = password.replace("l", "I")
        .replace("0", "O").toUpperCase();
      String upperCaseCaseUserName = account.replace("l", "I")
        .replace("0", "O").toUpperCase();
      if (StringUtils.contains(upperCaseCasePassword, upperCaseCaseUserName)) {
        throw new BadRequestException("密码中不得包含用户名或类似用户名的部分");
      }
    }
    Matcher matcher = PASSWORD_PATTERN.matcher(password);
    if (!matcher.matches()) {
      throw new BadRequestException("必须包含英文大小写、数字、特殊字符中的三种");
    }
  }
}
