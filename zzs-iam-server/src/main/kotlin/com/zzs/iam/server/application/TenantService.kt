package com.zzs.iam.server.application

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.server.domain.model.org.TenantDO
import com.zzs.iam.server.domain.model.org.TenantRepository
import com.zzs.iam.server.domain.model.org.TenantUserRepository
import com.zzs.iam.server.dto.args.CreateTenantArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author 宋志宗 on 2022/8/15
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class TenantService(
  private val tenantRepository: TenantRepository,
  private val tenantUserRepository: TenantUserRepository,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(TenantService::class.java)
  }

  /** 新增租户 */
  suspend fun create(platform: String, args: CreateTenantArgs): TenantDO {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val parent = args.parentId?.let {
      tenantRepository.findById(it)?.apply {
        if (this.platform != platform) {
          log.info(
            "{}新增租户失败, 因为父租户 [{} {}] 不属于传入的平台: {}",
            logPrefix, it, this.name, platform
          )
          throw BadRequestException("父租户所属平台如传入平台不一致")
        }
      } ?: run {
        log.info("{}新增租户失败, 父租户: {} 不存在", logPrefix, it)
        throw ResourceNotFoundException("父租户不存在")
      }
    }
    val name = args.name.requireNotBlank { "租户名称为空" }
    val address = args.address
    val note = args.note
    val tenantDo = TenantDO.create(platform, parent, name, address, note)
    tenantRepository.save(tenantDo)
    return tenantDo
  }

  /** 冻结租户 */
  suspend fun freeze(id: Long) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    tenantRepository.findById(id)?.let {
      if (it.isFrozen) {
        log.info("{}租户: [{} {}] 当前已是冻结状态", logPrefix, id, it.name)
        return
      }
      it.freeze()
      tenantRepository.save(it)
      log.info("{}成功冻结租户: [{} {}]", logPrefix, id, it.name)
    } ?: let {
      throw ResourceNotFoundException("冻结失败, 租户信息不存在")
    }
  }

  /** 解冻租户 */
  suspend fun unfreeze(id: Long) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    tenantRepository.findById(id)?.let {
      if (!it.isFrozen) {
        log.info("{}租户: [{} {}] 当前非冻结状态", logPrefix, id, it.name)
        return
      }
      it.unfreeze()
      tenantRepository.save(it)
      log.info("{}成功解冻租户: [{} {}]", logPrefix, id, it.name)
    } ?: let {
      throw ResourceNotFoundException("冻结失败, 租户信息不存在")
    }
  }

  /** 删除租户 */
  suspend fun delete(id: Long) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    tenantRepository.findById(id)?.let {
      // 判断是否存在子租户
      if (tenantRepository.existsByParentId(id)) {
        log.info("{}删除租户: [{} {}] 失败, 存在子租户", logPrefix, id, it.name)
        throw BadRequestException("删除租户失败, 存在子租户")
      }
      // 判断租户下是否存在用户
      if (tenantUserRepository.existsByTenantId(id)) {
        log.info("{}删除租户: [{} {}] 失败, 租户下存在用户", logPrefix, id, it.name)
        throw BadRequestException("删除租户失败, 租户下存在用户")
      }
      tenantRepository.delete(it)
      log.info("{}成功删除租户: [{} {}]", logPrefix, id, it.name)
    } ?: let {
      log.info("{}租户: {} 不存在", logPrefix, id)
    }
  }

  /** 获取指定租户下所有层级的子租户 */
  suspend fun getAllChild(id: Long): List<TenantDO> {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    return tenantRepository.findById(id)?.let {
      val parentRouter = it.parentRouter
      return tenantRepository.findAllChild(parentRouter)
    } ?: let {
      log.info("{}租户: {} 不存在", logPrefix, id)
      return emptyList()
    }
  }
}
