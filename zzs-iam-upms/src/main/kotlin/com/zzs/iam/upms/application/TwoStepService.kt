package com.zzs.iam.upms.application

import cn.idealframework2.autoconfigure.cache.CacheProperties
import cn.idealframework2.cache.coroutine.RedisCacheBuilderFactory
import cn.idealframework2.cache.serialize.JsonValueSerializer
import cn.idealframework2.cache.serialize.StringValueSerializer
import cn.idealframework2.exception.BadRequestException
import cn.idealframework2.lang.RandomStringUtils
import cn.idealframework2.trace.coroutine.TraceContextHolder
import com.zzs.iam.common.exception.TwoStepVerifyException
import com.zzs.iam.upms.domain.model.twostep.ActionRepository
import com.zzs.iam.upms.domain.model.twostep.TwoStepCfgDO
import com.zzs.iam.upms.domain.model.twostep.TwoStepCfgRepository
import com.zzs.iam.upms.domain.model.twostep.TwoStepConfig
import com.zzs.iam.upms.domain.model.user.UserProvider
import com.zzs.iam.upms.infrastructure.sender.EmailSender
import com.zzs.iam.upms.infrastructure.sender.SmsSender
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * 两步验证管理
 *
 * @author 宋志宗 on 2022/8/25
 */
@Service
class TwoStepService(
  private val smsSender: SmsSender,
  private val emailSender: EmailSender,
  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  private val userProvider: UserProvider,
  private val cacheProperties: CacheProperties,
  private val actionRepository: ActionRepository,
  private val twoStepCfgRepository: TwoStepCfgRepository,
  private val redisTemplate: ReactiveStringRedisTemplate,
  redisCacheBuilderFactory: RedisCacheBuilderFactory
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(TwoStepService::class.java)
  }

  /** 两步验证配置缓存 */
  private val configCache = redisCacheBuilderFactory
    .newBuilder<String, TwoStepConfig>(object : JsonValueSerializer<TwoStepConfig>() {})
    .multiLevel(1000, Duration.ofMinutes(10))
    .expireAfterWrite(Duration.ofMinutes(60))
    .build("two_step:config")
  private val twoStepCodeCache = redisCacheBuilderFactory
    .newBuilder<String, String>(StringValueSerializer.instance())
    .expireAfterWrite(Duration.ofMinutes(5))
    .build("two_step:code")

  /** 发送两步验证的邮箱验证码 */
  suspend fun sendEmailCode(userId: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val user = userProvider.getById(userId)
    val email = user.email ?: let {
      log.info("{}发送邮箱验证码失败, 用户 {} 未设置邮箱地址", logPrefix, userId)
      throw BadRequestException("未设置邮箱地址")
    }
    val code = RandomStringUtils.randomNumeric(6)
    emailSender.sendMailCode(email, "进行两步验证", code)
    twoStepCodeCache.put(userId, code)
  }

  /** 发送短信验证码 */
  suspend fun sendSmsCode(userId: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val user = userProvider.getById(userId)
    val phone = user.phone ?: let {
      log.info("{}发送短信验证码失败, 用户 {} 未设置手机号码", logPrefix, userId)
      throw BadRequestException("未设置手机号码")
    }
    val code = RandomStringUtils.randomNumeric(6)
    smsSender.sendCode(phone, code)
    twoStepCodeCache.put(userId, code)
  }

  /** 验证码认证 */
  suspend fun codeAuthenticate(platform: String, tenantId: Long?, userId: String, code: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val get = twoStepCodeCache.getIfPresent(userId) ?: let {
      log.info("{}两步验证失败, 未生成验证码", logPrefix)
      throw BadRequestException("未生成验证码")
    }
    if (get != code) {
      log.info("{}两步验证失败, 验证码错误", logPrefix)
      throw BadRequestException("验证码错误")
    }
    val verifiedKey = genVerifiedKey(platform, tenantId, userId)
    val config = loadConfig(platform, tenantId)
    val expireMinutes = config.expireMinutes
    val expire = Duration.ofMinutes(expireMinutes.toLong())
    redisTemplate.opsForValue().set(verifiedKey, "1", expire).awaitSingle()
    twoStepCodeCache.invalidate(userId)
  }

  /** MFA认证 */
  suspend fun mfaAuthenticate(platform: String, tenantId: Long?, userId: String, code: String) {

  }

  /** 两步验证校验 */
  suspend fun verify(platform: String, tenantId: Long?, userId: String, requestPath: String) {
    // 如果接口不需要两步验证, 则跳过后续逻辑
    val config = loadConfig(platform, tenantId)
    if (!config.needVerify(requestPath)) {
      return
    }

    // 如果用户已经通过了两步验证, 则跳过后续逻辑
    val verifiedKey = genVerifiedKey(platform, tenantId, userId)
    val cache = redisTemplate.opsForValue().get(verifiedKey).awaitSingleOrNull()
    if (cache == "1") {
      return
    }
    // 抛出两步验证异常
    throw TwoStepVerifyException()
  }

  /** 读取平台(租户)的两步验证配置 */
  suspend fun loadConfig(platform: String, tenantId: Long?) = coroutineScope {
    val key = genConfigKey(platform, tenantId)
    val config = configCache.get(key) {
      val async1 = async {
        actionRepository.findAllEnabledByPlatform(platform)
      }
      val async2 = async {
        twoStepCfgRepository.findByPlatformAndTenantId(platform, tenantId ?: -1L)
      }
      val actions = async1.await()
      val cfg = async2.await() ?: TwoStepCfgDO()
      TwoStepConfig().also { config ->
        config.expireMinutes = cfg.expireMinutes
        val enabled = cfg.isEnabled
        if (enabled) {
          val map = actions.associateByTo(HashMap()) { it.id }
          cfg.disableActions.forEach { map.remove(it) }
          val apis = map.values.map { it.apis }.flatMapTo(HashSet()) { it }
          config.apis = ArrayList(apis)
        }
      }
    }!!
    return@coroutineScope config
  }

  private fun genVerifiedKey(platform: String, tenantId: Long?, userId: String): String {
    val prefix = cacheProperties.formattedPrefix()
    return if (tenantId == null) {
      "${prefix}two_step:verified:$platform:$userId"
    } else {
      "${prefix}two_step:verified:$platform:$tenantId:$userId"
    }
  }

  private fun genConfigKey(platform: String, tenantId: Long?): String {
    val prefix = cacheProperties.formattedPrefix()
    return if (tenantId == null) {
      "${prefix}two_step:verified:$platform"
    } else {
      "${prefix}two_step:verified:$platform:$tenantId"
    }
  }
}
