package com.zzs.iam.gateway.filter.blacklist

import cn.idealframework2.spring.ExchangeUtils
import com.zzs.iam.gateway.common.FilterOrders
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 黑名单过滤器
 *
 * @author 宋志宗 on 2022/9/20
 */
class BlacklistFilter : Ordered, GlobalFilter {
  private var directBlackList = HashSet<String>()
  private var rangeBlacklist = ArrayList<Pair<Int, Int>>()

  override fun getOrder() = FilterOrders.BLACKLIST_FILTER_ORDER

  override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
    val remoteAddress = ExchangeUtils.getRemoteAddress(exchange)
    if (remoteAddress.isNullOrBlank()) {
      return chain.filter(exchange)
    }
    if (directBlackList.contains(remoteAddress)) {
      // TODO 命中黑名单, 拒绝访问
    }
    if (rangeBlacklist.isNotEmpty()) {

    }
    return chain.filter(exchange)
  }
}
