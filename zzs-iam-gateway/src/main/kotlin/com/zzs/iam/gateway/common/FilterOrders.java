package com.zzs.iam.gateway.common;

import org.springframework.core.Ordered;

/**
 * 过滤器顺序
 * <pre>
 * +-----------------------------+--------------+-----------+
 * |        filter name          |      order   |   type    |
 * +-----------------------------+--------------+-----------+
 * | RemoveCachedBodyFilter      |      HIGHEST |           |
 * +-----------------------------+--------------+-----------+
 * | AdaptCachedBodyGlobalFilter |      HIGHEST |           |
 * +-----------------------------+--------------+-----------+
 * | TraceFilter                 |  HIGHEST + 1 |  c-global |
 * +-----------------------------+--------------+-----------+
 * | NettyWriteResponseFilter    |           -1 |           |
 * +-----------------------------+--------------+-----------+
 * | ForwardPathFilter           |            0 |           |
 * +-----------------------------+--------------+-----------+
 * | GatewayMetricsFilter        |            0 |           |
 * +-----------------------------+--------------+-----------+
 * | RouteToRequestUrlFilter     |        10000 |           |
 * +-----------------------------+--------------+-----------+
 * | WeightCalculatorWebFilter   |        10001 |           |
 * +-----------------------------+--------------+-----------+
 * | LoadBalancerClientFilter    |        10100 |           |
 * +-----------------------------+--------------+-----------+
 * | WhitelistGatewayFilter      |        11001 | c-gateway |
 * +-----------------------------+--------------+-----------+
 * | BlacklistFilter             |        11002 |  c-global |
 * +-----------------------------+--------------+-----------+
 * | RateLimitFilter             |        11003 | c-gateway |
 * +-----------------------------+--------------+-----------+
 * | ReplayAttackFilter         |        11004 |  c-global |
 * +-----------------------------+--------------+-----------+
 * | AuthenticationGatewayFilter |        20000 | c-gateway |
 * +-----------------------------+--------------+-----------+
 * | IamN9eGatewayFilter         |        30000 | c-gateway |
 * +-----------------------------+--------------+-----------+
 * | ForwardTraceFilter          |   LOWEST - 2 |  c-global |
 * +-----------------------------+--------------+-----------+
 * | WebSocketRoutingFilter      |   LOWEST - 1 |           |
 * +-----------------------------+--------------+-----------+
 * | NettyRoutingFilter          |       LOWEST |           |
 * +-----------------------------+--------------+-----------+
 * | ForwardRoutingFilter        |       LOWEST |           |
 * +-----------------------------+--------------+-----------+
 * </pre>
 *
 * @author 宋志宗 on 2021/9/17
 */
public interface FilterOrders {
  /** 链路追踪过滤器 */
  int TRACE_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

  /** 白名单过滤器 */
  int WHITELIST_FILTER_ORDER = 10000;

  /** 黑名单过滤器 */
  int BLACKLIST_FILTER_ORDER = 10100;

  /** 限流过滤器 */
  int RATE_LIMIT_FILTER_ORDER = 10200;

  /** 重放攻击过滤器 */
  int REPLAY_ATTACK_FILTER_ORDER = 10300;

  /** 认证过滤器 */
  int AUTH_FILTER_ORDER = 20000;

  /** 转发链路追踪信息过滤器 */
  int FORWARD_TRACE_FILTER_ORDER = Ordered.LOWEST_PRECEDENCE - 2;
}
