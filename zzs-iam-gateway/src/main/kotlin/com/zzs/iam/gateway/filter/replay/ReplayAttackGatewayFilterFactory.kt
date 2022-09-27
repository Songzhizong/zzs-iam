package com.zzs.iam.gateway.filter.replay

import com.zzs.framework.autoconfigure.cache.CacheProperties
import com.zzs.framework.core.json.JsonUtils
import com.zzs.framework.core.spring.ExchangeUtils
import com.zzs.framework.core.trace.TraceContext
import com.zzs.framework.core.trace.reactive.TraceExchangeUtils
import com.zzs.framework.core.transmission.Result
import com.zzs.iam.gateway.common.FilterOrders
import com.zzs.iam.gateway.common.PathMatchers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

/**
 * 请求重放攻击过滤器
 *
 * @author 宋志宗 on 2022/9/26
 */
@Component
class ReplayAttackGatewayFilterFactory(
  private val cacheProperties: CacheProperties,
  private val redisTemplate: ReactiveStringRedisTemplate
) : AbstractGatewayFilterFactory<ReplayAttackGatewayFilterFactory.Config>(Config::class.java) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(ReplayAttackGatewayFilterFactory::class.java)
  }

  override fun name() = "ReplayAttack"

  override fun apply(config: Config): GatewayFilter {
    return ReplayAttackGatewayFilter(config, cacheProperties, redisTemplate)
  }

  class ReplayAttackGatewayFilter(
    private val config: Config,
    private val cacheProperties: CacheProperties,
    private val redisTemplate: ReactiveStringRedisTemplate
  ) : Ordered, GatewayFilter {

    override fun getOrder() = FilterOrders.REPLAY_ATTACK_FILTER_ORDER

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
      val contextOptional = TraceExchangeUtils.getTraceContext(exchange)
      val logPrefix = contextOptional.map { it.logPrefix }.orElse("")

      val request = exchange.request
      val requestPath = request.uri.path
      if (permitMatch(requestPath)) {
        log.debug("{}请求重放放行接口地址: {}", logPrefix, requestPath)
        return chain.filter(exchange)
      }
      val headerName = config.headerName
      val headers = request.headers
      val header = headers.getFirst(headerName)
      if (header.isNullOrBlank()) {
        log.info("{}{}未携带防重放攻击请求头", logPrefix, requestPath)
        return failureResult(
          exchange, contextOptional, "request header is blank: ${config.headerName}"
        )
      }
      val key = cacheProperties.formatKey("zzs_gw:replay_attack:$header")
      val expire = config.expire
      return redisTemplate.opsForValue().setIfAbsent(key, "1", expire)
        .defaultIfEmpty(false)
        .flatMap {
          if (it) {
            return@flatMap chain.filter(exchange)
          } else {
            log.info("{}防重放请求头: {} {} 重复使用", logPrefix, headerName, header)
            return@flatMap failureResult(exchange, contextOptional, "重复的请求")
          }
        }
    }

    private fun failureResult(
      exchange: ServerWebExchange,
      traceContext: Optional<TraceContext>,
      message: String
    ): Mono<Void> {
      val failure: Result<Void> = Result.failure("request.replay", message)
      traceContext.ifPresent { failure.traceId = it.traceId }
      val bytes: ByteArray = JsonUtils.toJsonString(failure).toByteArray(Charsets.UTF_8)
      val headers = HttpHeaders()
      headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
      return ExchangeUtils.writeResponse(exchange, HttpStatus.FORBIDDEN, headers, bytes)
    }

    private fun permitMatch(path: String): Boolean {
      val permitMatchers = config.permitMatchers
      for (permitMatcher in permitMatchers) {
        if (PathMatchers.match(permitMatcher, path)) {
          return true
        }
      }
      return false
    }
  }

  @Suppress("DuplicatedCode")
  class Config {

    var headerName = "x-ideal-request-id"

    /** 请求序列缓存时间  */
    var expire: Duration = Duration.ofMinutes(10)

    /** 放行地址列表  */
    var permitMatchers = HashSet<String>()

  }
}
