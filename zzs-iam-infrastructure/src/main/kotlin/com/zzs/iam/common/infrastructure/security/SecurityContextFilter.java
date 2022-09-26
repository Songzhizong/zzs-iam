package com.zzs.iam.common.infrastructure.security;

import com.zzs.framework.core.lang.StringUtils;
import com.zzs.iam.common.constants.IamHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/16
 */
@Configuration
public class SecurityContextFilter implements Ordered, WebFilter {

  @Override
  public int getOrder() {
    return 0;
  }

  @Nonnull
  @Override
  public Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
    HttpHeaders headers = exchange.getRequest().getHeaders();
    String platform = headers.getFirst(IamHeaders.PLATFORM);
    String multiTenantStr = headers.getFirst(IamHeaders.MULTI_TENANT);
    String userId = headers.getFirst(IamHeaders.USER_ID);
    if (StringUtils.isAnyBlank(platform, multiTenantStr, userId)) {
      return chain.filter(exchange);
    }
    assert platform != null;
    assert userId != null;
    String tenantIdStr = headers.getFirst(IamHeaders.TENANT_ID);
    boolean multiTenant = Boolean.parseBoolean(multiTenantStr);
    SecurityContext securityContext = new SecurityContext();
    securityContext.setPlatform(platform);
    securityContext.setMultiTenant(multiTenant);
    securityContext.setUserId(userId);
    if (StringUtils.isNotBlank(tenantIdStr)) {
      securityContext.setTenantId(Long.valueOf(tenantIdStr));
    }
    return chain.filter(exchange).contextWrite(ctx -> ctx.put(SecurityContextHolder.contextKey, securityContext));
  }
}
