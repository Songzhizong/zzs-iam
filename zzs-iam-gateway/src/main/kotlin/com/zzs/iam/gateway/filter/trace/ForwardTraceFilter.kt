package com.zzs.iam.gateway.filter.trace

import com.zzs.framework.core.trace.TraceConstants
import com.zzs.framework.core.trace.reactive.TraceContextHolder
import com.zzs.iam.gateway.common.FilterOrders
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * @author 宋志宗 on 2022/9/22
 */
@Component
class ForwardTraceFilter : Ordered, GlobalFilter {

  override fun getOrder() = FilterOrders.FORWARD_TRACE_FILTER_ORDER

  override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
    return TraceContextHolder.current()
      .flatMap { opt ->
        if (opt.isPresent) {
          val context = opt.get()
          val traceId = context.traceId
          val nextSpanId = context.nextSpanId()
          val request = exchange.request
          val httpRequest = request.mutate()
            .header(TraceConstants.TRACE_ID_HEADER_NAME, traceId)
            .header(TraceConstants.SPAN_ID_HEADER_NAME, nextSpanId)
            .build()
          return@flatMap chain.filter(exchange.mutate().request(httpRequest).build())
        }
        return@flatMap chain.filter(exchange)
      }
  }

}
