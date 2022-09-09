package com.zzs.iam.gateway.filter.auth

import com.zzs.framework.core.exception.VisibleException
import com.zzs.framework.core.json.toJsonString
import com.zzs.framework.core.spring.ExchangeUtils
import com.zzs.framework.core.transmission.Result
import com.zzs.iam.common.constants.IamHeaders
import com.zzs.iam.gateway.common.FilterOrders
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * @author 宋志宗 on 2022/8/17
 */
@Component
class IamAuthGatewayFilterFactory(
  @Qualifier("iamGatewayAntPathMatcher")
  private val antPathMatcher: AntPathMatcher,
  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  private val iamAuthenticateService: IamAuthenticateService
) : AbstractGatewayFilterFactory<IamAuthGatewayFilterFactory.Config>(Config::class.java) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(IamAuthGatewayFilterFactory::class.java)
  }

  override fun name(): String = "IamAuth"


  override fun apply(config: Config): GatewayFilter {
    return IamAuthGatewayFilter(config, antPathMatcher, iamAuthenticateService)
  }

  class IamAuthGatewayFilter(
    private val config: Config,
    private val antPathMatcher: AntPathMatcher,
    private val iamAuthenticateService: IamAuthenticateService
  ) : GatewayFilter, Ordered {

    override fun getOrder(): Int = FilterOrders.AUTH_FILTER_ORDER

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
      val request = exchange.request
      val requestPath = request.uri.path
      if (permitMatch(requestPath)) {
        log.debug("放行请求: {}", requestPath)
        return chain.filter(exchange)
      }
      return mono {
        val headers = request.headers
        val authorization = headers.getFirst(HttpHeaders.AUTHORIZATION)
        val tenantId = headers.getFirst(IamHeaders.TENANT_ID)?.toLong()
        val apiAuthenticate = apiAuthenticate(requestPath)
        iamAuthenticateService.authenticate(authorization, tenantId, requestPath, apiAuthenticate)
      }.flatMap { forwardHeaderMap ->
        val serverHttpRequest = request.mutate().headers { it.putAll(forwardHeaderMap) }.build()
        chain.filter(exchange.mutate().request(serverHttpRequest).build())
      }.onErrorResume { exception ->
        if (exception is VisibleException) {
          val httpStatus = exception.httpStatus
          val result = Result.exception<Void>(exception)
          val bytes = result.toJsonString().toByteArray(Charsets.UTF_8)
          ExchangeUtils.writeResponse(
            exchange,
            HttpStatus.valueOf(httpStatus),
            HttpHeaders().also { it.contentType = MediaType.APPLICATION_JSON },
            bytes
          )
        } else {
          val result = Result.exception<Void>(exception)
          val bytes = result.toJsonString().toByteArray(Charsets.UTF_8)
          ExchangeUtils.writeResponse(
            exchange,
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpHeaders().also { it.contentType = MediaType.APPLICATION_JSON },
            bytes
          )
        }
      }
    }

    private fun apiAuthenticate(path: String): Boolean {
      val excludeApiAuthMatchers = config.permitApiAuthMatchers
      for (matcher in excludeApiAuthMatchers) {
        if (antPathMatcher.match(matcher, path)) {
          log.debug("不需要API认证: {}", path)
          return false
        }
      }
      val apiAuthMatchers = config.apiAuthMatchers
      for (matcher in apiAuthMatchers) {
        if (antPathMatcher.match(matcher, path)) {
          log.debug("需要API认证: {}", path)
          return true
        }
      }
      log.debug("不需要API认证: {}", path)
      return false
    }


    private fun permitMatch(path: String): Boolean {
      val permitMatchers = config.permitMatchers
      for (permitMatcher in permitMatchers) {
        if (antPathMatcher.match(permitMatcher, path)) {
          return true
        }
      }
      return false
    }
  }


  class Config {

    /** 放行策略 */
    var permitMatchers = HashSet<String>()

    /** 需要通过api鉴权的接口清单 */
    var apiAuthMatchers = HashSet<String>()

    /** 不需要通过api鉴权的接口清单, 覆盖上面的配置 */
    var permitApiAuthMatchers = HashSet<String>()
  }
}
