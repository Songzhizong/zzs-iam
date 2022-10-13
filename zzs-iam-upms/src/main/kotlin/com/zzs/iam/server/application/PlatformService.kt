package com.zzs.iam.server.application

import cn.idealframework2.event.ReactiveTransactionalEventPublisher
import cn.idealframework2.event.coroutine.publishAndAwait
import cn.idealframework2.exception.BadRequestException
import cn.idealframework2.trace.coroutine.TraceContextHolder
import cn.idealframework2.utils.requireNotBlank
import com.zzs.iam.server.domain.model.org.PlatformDO
import com.zzs.iam.server.domain.model.org.PlatformRepository
import com.zzs.iam.server.dto.args.CreatePlatformArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 平台管理
 *
 * @author 宋志宗 on 2022/8/15
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class PlatformService(
  private val roleService: RoleService,
  private val platformRepository: PlatformRepository,
  private val transactionalEventPublisher: ReactiveTransactionalEventPublisher
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(PlatformService::class.java)
  }

  /** 新增平台 */
  suspend fun create(args: CreatePlatformArgs): PlatformDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val code = args.code.requireNotBlank { "平台编码为空" }.also {
      platformRepository.findByCode(it)?.apply {
        log.info("{}新增平台失败, 编码已被使用: {}", logPrefix, it)
        throw BadRequestException("平台编码已被使用")
      }
    }
    val name = args.name.requireNotBlank { "平台名称为空" }
    val multiTenant = args.isMultiTenant
    val tenantHasAllMenus = args.isTenantHasAllMenus
    val enableApiAuthenticate = args.isEnableApiAuthenticate
    val tuple = PlatformDO.create(
      code, name, multiTenant, tenantHasAllMenus, enableApiAuthenticate
    )
    val platformDo = tuple.value
    platformRepository.save(platformDo)

    // 非多租户平台创建超管角色
    if (!multiTenant) {
      roleService.createAdmin(platformDo, null, "超级管理员", "超级管理员")
    }
    transactionalEventPublisher.publishAndAwait(tuple)
    return platformDo
  }

  /** 删除平台 */
  suspend fun delete(code: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    platformRepository.findByCode(code)?.also {
      platformRepository.delete(it)
    } ?: let {
      log.info("{}平台: {} 不存在", logPrefix, code)
    }
  }
}
