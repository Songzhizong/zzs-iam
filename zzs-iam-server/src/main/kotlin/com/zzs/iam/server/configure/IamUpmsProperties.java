package com.zzs.iam.server.configure;

import com.zzs.iam.common.constants.UserAuthStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

/**
 * @author 宋志宗 on 2022/8/16
 */
@ConfigurationProperties("zzs-iam.upms")
public class IamUpmsProperties {

  /** 最近几次使用过的密码不能重复使用 */
  private int histPasswordLimit = 3;

  /** 密码过期天数 */
  private int passwordExpireDays = 90;

  /** 启用的两步验证策略 */
  @Nonnull
  private Set<UserAuthStrategy> userAuthStrategies = Collections.emptySet();

  public int getHistPasswordLimit() {
    return histPasswordLimit;
  }

  public IamUpmsProperties setHistPasswordLimit(int histPasswordLimit) {
    this.histPasswordLimit = histPasswordLimit;
    return this;
  }

  public int getPasswordExpireDays() {
    return passwordExpireDays;
  }

  public void setPasswordExpireDays(int passwordExpireDays) {
    this.passwordExpireDays = passwordExpireDays;
  }

  @Nonnull
  public Set<UserAuthStrategy> getUserAuthStrategies() {
    return userAuthStrategies;
  }

  public IamUpmsProperties setUserAuthStrategies(@Nonnull Set<UserAuthStrategy> userAuthStrategies) {
    this.userAuthStrategies = userAuthStrategies;
    return this;
  }
}
