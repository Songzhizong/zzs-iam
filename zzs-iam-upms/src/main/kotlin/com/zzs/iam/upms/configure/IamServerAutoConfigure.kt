package com.zzs.iam.upms.configure

import cn.idealframework2.spring.WebClients
import com.zzs.iam.upms.infrastructure.sender.SmsSender
import com.zzs.iam.upms.infrastructure.sender.impl.LogSmsSender
import com.zzs.iam.upms.infrastructure.wechat.MockWechatClient
import com.zzs.iam.upms.infrastructure.wechat.WechatClient
import com.zzs.iam.upms.infrastructure.wechat.WechatClientImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

/**
 * @author 宋志宗 on 2022/8/15
 */
@EnableScheduling
@ComponentScan("com.zzs.iam.upms")
@EntityScan("com.zzs.iam.upms.domain.model")
@EnableReactiveMongoRepositories("com.zzs.iam.upms")
@EnableConfigurationProperties(IamServerProperties::class)
class IamServerAutoConfigure {

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
    properties: IamServerProperties,
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
