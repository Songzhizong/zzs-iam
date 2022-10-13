package com.zzs.iam.upms.domain.model.authorization;

import cn.idealframework2.exception.UnauthorizedException;
import cn.idealframework2.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/15
 */
public class BearerAuthorization implements Authorization {
  public static final String TYPE = "Bearer";
  private static final Log log = LogFactory.getLog(BearerAuthorization.class);
  private static final String TOKEN_PREFIX = TYPE + " ";
  private static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();
  @Nonnull
  private final String token;

  public BearerAuthorization(@Nonnull String token) {
    this.token = token;
  }

  public static boolean isSupport(@Nonnull String authorization) {
    return StringUtils.startsWith(authorization, TOKEN_PREFIX);
  }

  @Nonnull
  public static BearerAuthorization of(@Nonnull String authorization) {
    boolean support = isSupport(authorization);
    if (!support) {
      log.info("Invalid authorization for BearerAuthorization: " + authorization);
      throw new UnauthorizedException("Invalid authorization for BearerAuthorization");
    }
    String token = authorization.substring(TOKEN_PREFIX_LENGTH);
    return new BearerAuthorization(token);
  }

  @Nonnull
  public String getToken() {
    return token;
  }
}
