package com.zzs.iam.server.application

import com.zzs.framework.autoconfigure.cache.CacheProperties
import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.framework.core.spring.RedisTemplateUtils
import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import com.zzs.framework.core.utils.requireNonnull
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.server.domain.model.front.MenuDo
import com.zzs.iam.server.domain.model.front.MenuRepository
import com.zzs.iam.server.domain.model.front.TerminalRepository
import com.zzs.iam.server.dto.args.CreateMenuArgs
import com.zzs.iam.server.dto.args.UpdateMenuArgs
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*

/**
 * 菜单管理
 *
 * @author 宋志宗 on 2022/8/16
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class MenuService(
  private val menuRepository: MenuRepository,
  private val cacheProperties: CacheProperties,
  private val terminalRepository: TerminalRepository,
  private val redisTemplate: ReactiveStringRedisTemplate,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(MenuService::class.java)
    private val lockValue = UUID.randomUUID().toString().replace("-", "")
  }

  suspend fun create(args: CreateMenuArgs): MenuDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    // 校验终端
    val terminal = args.terminal.requireNonnull { "终端编码为空" }.let {
      terminalRepository.findByCode(it) ?: run {
        log.info("{}终端: {} 不存在", logPrefix, it)
        throw ResourceNotFoundException("终端不存在")
      }
    }
    // 校验父菜单
    val parent = args.parentId?.let {
      menuRepository.findById(it)?.apply {
        if (terminal.platform != this.platform) {
          throw BadRequestException("终端与父菜单不属于同一平台")
        }
      } ?: run {
        log.info("{}父菜单: {} 不存在", logPrefix, it)
        throw ResourceNotFoundException("父菜单不存在")
      }
    }
    val name = args.name.requireNotBlank { "菜单名称为空" }
    val type = args.type.requireNonnull { "菜单类型为空" }
    val order = args.order
    val icon = args.icon
    val selectedIcon = args.selectedIcon
    val url = args.url
    val path = args.path
    val apis = args.apis?.toSet()
    val menuDo = MenuDo.create(
      parent, terminal, name, type, order, icon, selectedIcon, url, path, apis
    )
    menuRepository.save(menuDo)
    return menuDo
  }

  /** 修改菜单信息 */
  suspend fun update(id: Long, args: UpdateMenuArgs): MenuDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val menuDo = menuRepository.findById(id) ?: let {
      log.info("{}修改菜单信息失败, 菜单 {} 不存在", logPrefix, id)
      throw ResourceNotFoundException("菜单信息不存在")
    }
    menuDo.updated(args)
    menuRepository.save(menuDo)
    return menuDo
  }

  /** 变更父菜单 */
  suspend fun changeParent(id: Long, parentId: Long?): MenuDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val menuDo = menuRepository.findById(id) ?: let {
      log.info("{}修改菜单信息失败, 菜单 {} 不存在", logPrefix, id)
      throw ResourceNotFoundException("菜单信息不存在")
    }
    // 防止并发修改父菜单
    val terminal = menuDo.terminal
    val prefix = cacheProperties.formattedPrefix()
    val lock = "${prefix}iam:menu:change_parent:$terminal"
    val tryLock = redisTemplate.opsForValue()
      .setAndAwait(lock, lockValue, Duration.ofSeconds(10))
    if (!tryLock) {
      throw BadRequestException("当前操作正在进行中")
    }
    // 获取所有的子菜单
    val parentRouter = menuDo.generateRouter()
    val childList = menuRepository.findAllChild(parentRouter)
    val group = childList.groupBy { it.parentId }

    try {
      if (menuDo.parentId == parentId) {
        log.info("{}父菜单未发生变更", logPrefix)
        return menuDo
      }
      var parent: MenuDo? = null
      if (parentId != null) {
        parent = menuRepository.findById(parentId) ?: let {
          log.info("{}变更父菜单失败, 所选的菜单: {} 不存在", logPrefix, parentId)
          throw ResourceNotFoundException("所选的父菜单不存在")
        }
      }
      // 修改父菜单并保存
      menuDo.changeParent(parent)
      menuRepository.save(menuDo)
      // 如果当前菜单存在子菜单, 则递归修改所有子菜单的父菜单
      if (childList.isNotEmpty()) {
        changeParent(menuDo, group)
        menuRepository.saveAll(childList)
      }
      return menuDo
    } finally {
      RedisTemplateUtils.unlock(redisTemplate, lock, lockValue).awaitSingle()
    }
  }

  /** 递归修改父菜单 */
  private fun changeParent(menuDo: MenuDo, childMap: Map<Long, List<MenuDo>>) {
    val childList = childMap[menuDo.id]
    if (childList.isNullOrEmpty()) {
      return
    }
    childList.forEach {
      it.changeParent(menuDo)
      changeParent(it, childMap)
    }
  }

  suspend fun delete(id: Long, force: Boolean = false) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val existsChild = menuRepository.existsByParentId(id)
    if (!force && existsChild) {
      log.info("{}删除菜单: {} 失败, 该菜单下存在子菜单", logPrefix, id)
      throw BadRequestException("删除失败, 该菜单下存在子菜单")
    }
    menuRepository.findById(id)?.also {
      menuRepository.delete(it)
      if (force && existsChild) {
        val count = menuRepository.deleteAllChildByRouter(it.generateRouter())
        log.info("{}成功删除菜单: [{} {}] 以及其子菜单 {}条", logPrefix, id, it.name, count)
      } else {
        log.info("{}成功删除菜单: [{} {}]", logPrefix, id, it.name)
      }
    } ?: let {
      log.info("{}菜单: {} 不存在", logPrefix, id)
    }
  }
}
