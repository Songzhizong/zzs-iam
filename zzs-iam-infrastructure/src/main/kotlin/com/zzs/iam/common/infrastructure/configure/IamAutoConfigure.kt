package com.zzs.iam.common.infrastructure.configure

import com.zzs.framework.core.spring.WebClients
import com.zzs.iam.common.infrastructure.sender.SmsSender
import com.zzs.iam.common.infrastructure.sender.impl.LogSmsSender
import com.zzs.iam.common.infrastructure.wechat.MockWechatClient
import com.zzs.iam.common.infrastructure.wechat.WechatClient
import com.zzs.iam.common.infrastructure.wechat.WechatClientImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

/**
 * @author 宋志宗 on 2022/8/15
 */
@ComponentScan("com.zzs.iam.common.infrastructure")
@EnableConfigurationProperties(IamProperties::class)
class IamAutoConfigure {

  @Bean("iamWebClient")
  fun webClient(): WebClient {
    return WebClients.webClient { options ->
      @Suppress("UsePropertyAccessSyntax")
      options.setKeepAlive(true)
        .setResponseTimeout(Duration.ofSeconds(5))
    }
  }

  @Bean
  fun wechatClient(
    properties: IamProperties,
    @Qualifier("iamWebClient")
    webClient: WebClient
  ): WechatClient {
    val wechat = properties.wechat
    if (wechat.mock.isEnabled) {
      return MockWechatClient(wechat)
    }
    return WechatClientImpl(webClient, wechat)
  }

  @Bean
  @ConditionalOnMissingBean
  fun smsSender(): SmsSender {
    return LogSmsSender()
  }
}
