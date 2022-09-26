package com.zzs.iam.server.domain.model.authorization.token;

import com.zzs.iam.server.domain.model.authorization.Authentication;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class RefreshTokenDO {

  @Nonnull
  private String value = "";

  /** 有效时长, 单位秒 */
  private int validity;

  private Authentication authentication;

  @Nonnull
  public static RefreshTokenDO create(int validity,
                                      @Nonnull Authentication authentication) {
    RefreshTokenDO refreshTokenDo = new RefreshTokenDO();
    refreshTokenDo.setValue(UUID.randomUUID().toString().replace("-", ""));
    refreshTokenDo.setValidity(validity);
    refreshTokenDo.setAuthentication(authentication);
    return refreshTokenDo;
  }

  @Nonnull
  public String getValue() {
    return value;
  }

  public void setValue(@Nonnull String value) {
    this.value = value;
  }

  public int getValidity() {
    return validity;
  }

  public void setValidity(int validity) {
    this.validity = validity;
  }

  public Authentication getAuthentication() {
    return authentication;
  }

  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
  }
}
