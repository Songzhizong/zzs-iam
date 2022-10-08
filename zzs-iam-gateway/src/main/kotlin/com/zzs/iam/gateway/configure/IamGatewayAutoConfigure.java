package com.zzs.iam.gateway.configure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * @author 宋志宗 on 2022/10/8
 */
@ComponentScan("com.zzs.iam.gateway")
public class IamGatewayAutoConfigure {

  @Bean(RedisRateLimiter.REDIS_SCRIPT_NAME)
  @SuppressWarnings({"unchecked", "rawtypes"})
  public RedisScript<List<Long>> redisRequestRateLimiterScript() {
    DefaultRedisScript redisScript = new DefaultRedisScript<>();
    redisScript.setScriptSource(
      new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
    redisScript.setResultType(List.class);
    return redisScript;
  }

  @Bean
  public RedisRateLimiter redisRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                           @Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
                                           ConfigurationService configurationService) {
    return new RedisRateLimiter(redisTemplate, redisScript, configurationService);
  }

}
