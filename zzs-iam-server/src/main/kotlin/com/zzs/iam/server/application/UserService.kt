package com.zzs.iam.server.application

import cn.idealframework2.cache.coroutine.RedisCacheBuilderFactory
import cn.idealframework2.cache.serialize.JsonValueSerializer
import cn.idealframework2.event.ReactiveTransactionalEventPublisher
import cn.idealframework2.event.coroutine.publishAndAwait
import cn.idealframework2.exception.BadRequestException
import cn.idealframework2.exception.ForbiddenException
import cn.idealframework2.exception.ResourceNotFoundException
import cn.idealframework2.trace.coroutine.TraceContextHolder
import cn.idealframework2.utils.requireNotBlank
import com.zzs.iam.common.password.PasswordEncoder
import com.zzs.iam.common.pojo.User
import com.zzs.iam.server.configure.IamServerProperties
import com.zzs.iam.server.domain.model.user.HistPasswordDO
import com.zzs.iam.server.domain.model.user.HistPasswordRepository
import com.zzs.iam.server.domain.model.user.UserDO
import com.zzs.iam.server.domain.model.user.UserRepository
import com.zzs.iam.server.dto.args.RegisterUserArgs
import com.zzs.iam.server.dto.args.UpdateUserArgs
import com.zzs.iam.server.infrastructure.CheckUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import kotlin.math.min

/**
 * 用户管理
 *
 * @author 宋志宗 on 2022/8/15
 */
@Service("iamUserUserService")
@Transactional(rollbackFor = [Throwable::class])
class UserService(
  private val userRepository: UserRepository,
  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  private val passwordEncoder: PasswordEncoder,
  cacheBuilderFactory: RedisCacheBuilderFactory,
  private val userProperties: IamServerProperties,
  private val histPasswordRepository: HistPasswordRepository,
  private val transactionalEventPublisher: ReactiveTransactionalEventPublisher,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
  }

  private val userCache =
    cacheBuilderFactory.newBuilder<String, User>(object : JsonValueSerializer<User>() {})
      .cacheNull(Duration.ofSeconds(30))
      .expireAfterWrite(Duration.ofMinutes(30), Duration.ofMinutes(60))
      .build("user_info")

  /**
   * 注册账号
   */
  suspend fun register(args: RegisterUserArgs): UserDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val name = args.name
    val nickname = args.nickname
    val account = args.account?.also {
      userRepository.findByUniqueIdent(it)?.apply {
        log.info("{}注册失败,账号已被使用: {}", logPrefix, it)
        throw BadRequestException("该账号已被使用")
      }
    }
    val phone = args.phone?.also {
      userRepository.findByUniqueIdent(it)?.apply {
        log.info("{}注册失败,手机号已被使用: {}", logPrefix, it)
        throw BadRequestException("该手机号已被使用")
      }
    }
    val email = args.email?.also {
      userRepository.findByUniqueIdent(it)?.apply {
        log.info("{}注册失败,邮箱已被使用: {}", logPrefix, it)
        throw BadRequestException("该邮箱已被使用")
      }
    }
    val password = args.password.requireNotBlank { "密码为空" }.let {
      CheckUtils.checkPassword(it, account)
      passwordEncoder.encode(it)
    }
    val tuple = UserDO.create(
      name, nickname, account, phone, email
    )
    val userDo = tuple.value
    userDo.setupPassword(password)
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(tuple)
    userCache.put(userDo.id.toString(), userDo.toUser())
    return userDo
  }

  /**
   * 冻结用户
   */
  suspend fun freeze(id: Long): UserDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(id) ?: let {
      log.info("{}用户: {} 不存在", logPrefix, id)
      throw ResourceNotFoundException("用户不存在")
    }
    val suppliers = userDo.freeze()
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
    return userDo
  }

  /**
   * 解除冻结
   */
  suspend fun unfreeze(id: Long): UserDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(id) ?: let {
      log.info("{}用户: {} 不存在", logPrefix, id)
      throw ResourceNotFoundException("用户不存在")
    }
    val suppliers = userDo.unfreeze()
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
    return userDo
  }

  /** 通过ID获取用户信息 */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  suspend fun getById(id: Long): User {
    return userCache.get(id.toString()) { userRepository.findById(id)?.toUser() }
      ?: throw ResourceNotFoundException("用户不存在")
  }


  /**
   * 修改手机号码
   *
   * @param userId      用户ID
   * @param newPassword 新密码
   * @param oldPassword 用于验证身份的原密码
   */
  suspend fun changePassword(userId: Long, newPassword: String, oldPassword: String? = null) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(userId) ?: let {
      log.info("{}修改密码失败, 用户: {} 不存在", logPrefix, userId)
      throw ResourceNotFoundException("用户信息不存在")
    }
    val oldEncodedPassword = userDo.password
    // 新密码不能与原密码一致
    if (oldPassword != null) {
      val matches = passwordEncoder.matches(oldPassword, oldEncodedPassword)
      if (!matches) {
        log.info("{}用户 [{} {}] 修改密码失败, 原密码输入错误", logPrefix, userId, userDo.name)
        throw ForbiddenException("原密码输入错误")
      }
    }
    // 最近已经使用过的密码不能重复使用
    val histPasswordLimit = min(userProperties.histPasswordLimit, 5)
    if (histPasswordLimit > 0) {
      val histPasswords = histPasswordRepository
        .findUserLatest(userId, histPasswordLimit)
      for (histPassword in histPasswords) {
        val histEncodedPassword = histPassword.password
        if (passwordEncoder.matches(newPassword, histEncodedPassword)) {
          log.info(
            "{}用户: [{} {}] 修改密码失败, 新输入的密码最近已经使用过",
            logPrefix, userId, userDo.name
          )
          throw BadRequestException("该密码最近已经使用过")
        }
      }
    }
    // 修改密码
    val encode = passwordEncoder.encode(newPassword)
    val suppliers = userDo.setupPassword(encode)
    val histPasswordDo = HistPasswordDO.create(userId, oldEncodedPassword)
    userRepository.save(userDo)
    histPasswordRepository.save(histPasswordDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
  }

  suspend fun resetPassword(userId: Long) {

  }

  /**
   * 修改账号
   *
   * @param userId     用户ID
   * @param newAccount 新的账号
   * @param password   用于验证身份的用户密码
   */
  suspend fun changeAccount(userId: Long, newAccount: String, password: String? = null) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(userId) ?: let {
      log.info("{}修改账号失败, 用户: {} 不存在", logPrefix, userId)
      throw ResourceNotFoundException("用户信息不存在")
    }
    if (password != null) {
      val encodedPassword = userDo.password
      val matches = passwordEncoder.matches(password, encodedPassword)
      if (!matches) {
        log.info("{}用户 [{} {}] 修改账号码失败, 密码输入错误", logPrefix, userId, userDo.name)
        throw ForbiddenException("密码输入错误")
      }
    }
    val suppliers = userDo.changeAccount(newAccount)
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
  }

  /**
   * 修改手机号码
   *
   * @param userId   用户ID
   * @param newPhone 新手机号码
   * @param password 用于验证身份的用户密码
   */
  suspend fun changePhone(userId: Long, newPhone: String, password: String? = null) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(userId) ?: let {
      log.info("{}修改手机号失败, 用户: {} 不存在", logPrefix, userId)
      throw ResourceNotFoundException("用户信息不存在")
    }
    if (password != null) {
      val encodedPassword = userDo.password
      val matches = passwordEncoder.matches(password, encodedPassword)
      if (!matches) {
        log.info("{}用户 [{} {}] 修改手机号码失败, 密码输入错误", logPrefix, userId, userDo.name)
        throw ForbiddenException("密码输入错误")
      }
    }
    val suppliers = userDo.changePhone(newPhone)
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
  }

  /**
   * 修改邮箱
   *
   * @param userId   用户ID
   * @param newEmail 新邮箱
   * @param password 用于验证身份的个人密码
   */
  suspend fun changeEmail(userId: Long, newEmail: String, password: String? = null) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(userId) ?: let {
      log.info("{}修改邮箱失败, 用户: {} 不存在", logPrefix, userId)
      throw ResourceNotFoundException("用户信息不存在")
    }
    if (password != null) {
      val encodedPassword = userDo.password
      val matches = passwordEncoder.matches(password, encodedPassword)
      if (!matches) {
        log.info("{}用户 [{} {}] 修改邮箱失败, 密码输入错误", logPrefix, userId, userDo.name)
        throw ForbiddenException("密码输入错误")
      }
    }
    val suppliers = userDo.changeEmail(newEmail)
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
  }

  /** 选择性更新用户信息 */
  suspend fun selectivityUpdate(userId: Long, args: UpdateUserArgs): UserDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findById(userId) ?: let {
      log.info("{}用户信息不存在: {}", logPrefix, userId)
      throw ResourceNotFoundException("用户信息不存在")
    }
    val suppliers = userDo.selectivityUpdate(args)
    userRepository.save(userDo)
    transactionalEventPublisher.publishAndAwait(suppliers)
    return userDo
  }

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  suspend fun authenticate(uniqueIdent: String, password: String): UserDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val userDo = userRepository.findByUniqueIdent(uniqueIdent) ?: let {
      log.info("{}认证失败, 用户: {} 不存在", logPrefix, uniqueIdent)
      throw BadRequestException("用户名或密码错误")
    }
    val encodedPassword = userDo.password
    val matches = passwordEncoder.matches(password, encodedPassword)
    if (!matches) {
      log.info("{}认证失败, 用户: {} 输入密码错误", logPrefix, uniqueIdent)
      throw BadRequestException("用户名或密码错误")
    }
    if (userDo.isFrozen) {
      log.info("{}用户: [{} {}] 已被冻结", logPrefix, userDo.id, userDo.name)
      throw ForbiddenException("用户已被冻结")
    }
    return userDo
  }
}
