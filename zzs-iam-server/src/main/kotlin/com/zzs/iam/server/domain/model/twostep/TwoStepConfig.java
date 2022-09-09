package com.zzs.iam.server.domain.model.twostep;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

/**
 * @author 宋志宗 on 2022/8/26
 */
public class TwoStepConfig {

  /** 两步验证有效期, 单位分钟 */
  private int expireMinutes = 10;

  /** 需要进行两步验证的API列表 */
  @Nonnull
  private Set<String> apis = Collections.emptySet();


  public boolean needVerify(@Nonnull String path) {
    return apis.contains(path);
  }

  public int getExpireMinutes() {
    return expireMinutes;
  }

  public TwoStepConfig setExpireMinutes(int expireMinutes) {
    this.expireMinutes = expireMinutes;
    return this;
  }

  @Nonnull
  public Set<String> getApis() {
    return apis;
  }

  public TwoStepConfig setApis(@Nonnull Set<String> apis) {
    this.apis = apis;
    return this;
  }
}
