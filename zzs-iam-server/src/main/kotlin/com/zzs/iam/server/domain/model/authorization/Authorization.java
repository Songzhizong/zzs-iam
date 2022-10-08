package com.zzs.iam.server.domain.model.authorization;

import cn.idealframework2.exception.UnauthorizedException;
import cn.idealframework2.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/15
 */
public interface Authorization {
  Log log = LogFactory.getLog(Authorization.class);

  /**
   * 获取Authorization
   *
   * @param exchange {@link ServerWebExchange}
   * @return {@link Authorization}
   */
  @Nonnull
  static Authorization get(@Nonnull ServerWebExchange exchange) {
    String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.isBlank(authorization)) {
      throw new UnauthorizedException("Authorization is blank");
    }
    return pare(authorization);
  }

  /**
   * 解析Authorization请求头
   *
   * @param authorizationHeader Authorization http header
   * @return {@link Authorization}
   */
  @Nonnull
  static Authorization pare(@Nonnull String authorizationHeader) {
    if (StringUtils.isBlank(authorizationHeader)) {
      log.info("Authorization is blank");
      throw new UnauthorizedException("Authorization is blank");
    }
    if (BearerAuthorization.isSupport(authorizationHeader)) {
      return BearerAuthorization.of(authorizationHeader);
    }
    log.info("Invalid authorizationToken: " + authorizationHeader);
    throw new UnauthorizedException("Invalid authorizationToken");
  }
}
