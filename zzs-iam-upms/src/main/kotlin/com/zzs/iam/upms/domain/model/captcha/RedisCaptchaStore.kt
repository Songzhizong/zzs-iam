package com.zzs.iam.upms.domain.model.captcha

import cn.idealframework2.autoconfigure.cache.CacheProperties
import cn.idealframework2.json.JsonUtils
import cn.idealframework2.json.parseJson
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

/**
 * @author 宋志宗 on 2022/8/26
 */
@Repository
class RedisCaptchaStore(
  private val cacheProperties: CacheProperties,
  private val redisTemplate: ReactiveStringRedisTemplate
) : CaptchaStore {

  override suspend fun save(key: String, captcha: Captcha, timeout: Duration) {
    val redisKey = genRedisKey(key)
    val value = JsonUtils.toJsonString(captcha)
    redisTemplate.opsForValue().set(redisKey, value, timeout).awaitSingle()
  }

  override suspend fun delete(key: String) {
    val redisKey = genRedisKey(key)
    redisTemplate.delete(redisKey).awaitSingle()
  }

  override suspend fun get(key: String): Captcha? {
    val redisKey = genRedisKey(key)
    val value = redisTemplate.opsForValue().get(redisKey).awaitSingleOrNull()
    if (value.isNullOrBlank()) {
      return null
    }
    return value.parseJson(Captcha::class.java)
  }

  private fun genRedisKey(key: String): String {
    val prefix = cacheProperties.formattedPrefix()
    return prefix + key
  }
}
