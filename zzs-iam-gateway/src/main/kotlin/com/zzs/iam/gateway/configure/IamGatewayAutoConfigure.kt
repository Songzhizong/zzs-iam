package com.zzs.iam.gateway.configure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.util.AntPathMatcher

/**
 * @author 宋志宗 on 2022/8/16
 */
@ComponentScan("com.zzs.iam.gateway")
class IamGatewayAutoConfigure {


  @Bean("iamGatewayAntPathMatcher")
  fun antPathMatcher(): AntPathMatcher {
    return AntPathMatcher()
  }
}
