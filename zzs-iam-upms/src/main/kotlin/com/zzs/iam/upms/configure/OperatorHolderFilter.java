package com.zzs.iam.upms.configure;

import cn.idealframework2.lang.StringUtils;
import cn.idealframework2.trace.Operator;
import cn.idealframework2.trace.reactive.OperatorHolder;
import com.zzs.iam.common.constants.IamHeaders;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/9/23
 */
@Component
public class OperatorHolderFilter implements Ordered, WebFilter, OperatorHolder {
  private static final String OPERATOR_KEY = "$$(zzs.iam.operator)_$$";

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Nonnull
  @Override
  public Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    HttpHeaders headers = request.getHeaders();
    String platform = headers.getFirst(IamHeaders.PLATFORM);
    String tenantId = headers.getFirst(IamHeaders.TENANT_ID);
    String userId = headers.getFirst(IamHeaders.USER_ID);
    Operator operator = null;
    if (StringUtils.isNotBlank(userId)) {
      operator = new Operator();
      operator.setUserId(userId);
      if (platform != null) {
        operator.setPlatform(platform);
      }
      if (tenantId != null) {
        operator.setTenantId(tenantId);
      }
    }
    Operator finalOperator = operator;
    return chain.filter(exchange).contextWrite(ctx -> {
      if (finalOperator == null) {
        return ctx;
      }
      return ctx.put(OPERATOR_KEY, finalOperator);
    });
  }

  @Nonnull
  @Override
  public Mono<Operator> get() {
    return Mono.deferContextual(ctx -> {
      if (!ctx.hasKey(OPERATOR_KEY)) {
        return Mono.empty();
      }
      Object o = ctx.get(OPERATOR_KEY);
      if (o instanceof Operator operator) {
        return Mono.just(operator);
      }
      return Mono.empty();
    });
  }
}
