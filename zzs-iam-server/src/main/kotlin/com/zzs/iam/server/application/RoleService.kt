package com.zzs.iam.server.application

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.ForbiddenException
import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.framework.core.json.toJsonString
import com.zzs.framework.core.trace.coroutine.Operations
import com.zzs.framework.core.trace.coroutine.TraceContextHolder
import com.zzs.framework.core.utils.requireNonnull
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.constants.RoleType
import com.zzs.iam.common.pojo.SimpleMenu
import com.zzs.iam.server.domain.model.front.MenuRepository
import com.zzs.iam.server.domain.model.front.TerminalRepository
import com.zzs.iam.server.domain.model.org.PlatformDo
import com.zzs.iam.server.domain.model.org.PlatformRepository
import com.zzs.iam.server.domain.model.org.TenantDo
import com.zzs.iam.server.domain.model.org.TenantRepository
import com.zzs.iam.server.domain.model.role.RoleDo
import com.zzs.iam.server.domain.model.role.RoleMenuRelDo
import com.zzs.iam.server.domain.model.role.RoleMenuRelRepository
import com.zzs.iam.server.domain.model.role.RoleRepository
import com.zzs.iam.server.dto.args.AssignMenuArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 角色管理
 *
 * @author 宋志宗 on 2022/8/16
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class RoleService(
  private val menuRepository: MenuRepository,
  private val roleRepository: RoleRepository,
  private val tenantRepository: TenantRepository,
  private val terminalRepository: TerminalRepository,
  private val platformRepository: PlatformRepository,
  private val roleMenuRelRepository: RoleMenuRelRepository,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(RoleService::class.java)
  }

  /** 创建超管角色 (新增平台或者租户时调用此方法) */
  suspend fun createAdmin(
    platform: PlatformDo,
    tenantId: Long?,
    name: String,
    note: String?
  ): RoleDo {
    return create(platform, tenantId, RoleType.ADMIN, name, note)
  }

  /** 创建普通角色 */
  suspend fun createGeneral(
    platform: String,
    tenantId: Long?,
    name: String,
    note: String?
  ): RoleDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    Operations.log()?.also {
      it.details = "新增角色[$name]"
      it.after = mapOf("name" to name, "note" to note).toJsonString()
    }
    val platformDo = platformRepository.findByCode(platform) ?: let {
      log.info("{}新增角色失败, 平台 : {} 不存在", logPrefix, platform)
      throw ResourceNotFoundException("平台不存在")
    }
    return create(platformDo, tenantId, RoleType.GENERAL, name, note)
  }

  /** 创建角色 */
  private suspend fun create(
    platformDo: PlatformDo,
    tenantId: Long?,
    type: RoleType,
    name: String,
    note: String?
  ): RoleDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    var tenant: TenantDo? = null
    if (platformDo.isMultiTenant) {
      tenantId.requireNonnull { "租户ID为空" }
      tenant = tenantRepository.findById(tenantId) ?: let {
        log.info("{}新增角色失败, 租户 : {} 不存在", logPrefix, tenantId)
        throw ResourceNotFoundException("租户不存在")
      }
    }
    val roleDo = RoleDo.create(platformDo, tenant, type, name, note)
    roleRepository.save(roleDo)
    return roleDo
  }

  /** 更新角色信息 */
  suspend fun update(
    id: Long,
    name: String,
    note: String?,
    platform: String? = null,
    tenantId: Long? = null
  ): RoleDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val roleDo = checkAndGetRole(id, platform, tenantId)
    val curName = roleDo.name
    Operations.log()?.also {
      it.details = "修改[$curName]的基本信息"
      it.before = mapOf("name" to curName, "note" to roleDo.note).toJsonString()
      it.after = mapOf("name" to name, "note" to note).toJsonString()
    }
    if (roleDo.type == RoleType.ADMIN && curName != name) {
      log.info("{}无法修改超管角色的名称", logPrefix)
      throw BadRequestException("无法修改超管角色的名称")
    }
    roleDo.name = name
    roleDo.note = note
    roleRepository.save(roleDo)
    return roleDo
  }

  /** 将角色设置我基础角色 */
  suspend fun setAsBasic(id: Long, platform: String? = null, tenantId: Long? = null) {
    val roleDo = checkAndGetRole(id, platform, tenantId)
    Operations.log()?.also {
      it.details = "将[${roleDo.name}]设为基础角色"
      it.before = if (roleDo.isBasic) "基础角色" else "非基础角色"
      it.after = "基础角色"
    }
    roleDo.setAsBasic()
    roleRepository.save(roleDo)
  }

  /** 取消基础角色 */
  suspend fun cancelBasic(id: Long, platform: String? = null, tenantId: Long? = null) {
    val roleDo = checkAndGetRole(id, platform, tenantId)
    Operations.log()?.also {
      it.details = "将[${roleDo.name}]设为非基础角色"
      it.before = if (roleDo.isBasic) "基础角色" else "非基础角色"
      it.after = "非基础角色"
    }
    roleDo.cancelBasic()
    roleRepository.save(roleDo)
  }

  /** 删除菜单 */
  suspend fun delete(id: Long, platform: String? = null, tenantId: Long? = null) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    roleRepository.findById(id)?.also {
      Operations.details("删除[${it.name}]")
      if (platform != null && platform != it.platform) {
        throw ForbiddenException("没有此角色的管理权限")
      }
      if (tenantId != null && tenantId != it.tenantId) {
        throw ForbiddenException("没有此角色的管理权限")
      }
      roleRepository.delete(it)
      log.info("{}成功删除角色: [{} {}]", logPrefix, id, it.name)
    } ?: let {
      log.info("{}角色: {} 不存在", logPrefix, id)
    }
  }

  /** 获取可分配的菜单列表 */
  suspend fun assignableMenus(
    platform: String,
    tenantId: Long?,
    terminal: String
  ): List<SimpleMenu> {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val terminalDo = terminalRepository.findByCode(terminal) ?: let {
      log.info("{}终端: {} 不存在", logPrefix, terminal)
      throw ResourceNotFoundException("指定的终端不存在")
    }
    if (terminalDo.platform != platform) {
      log.info("{}终端: {} 不属于平台: {}", logPrefix, terminal, platform)
      throw ForbiddenException("没有此终端权限")
    }
    if (tenantId == null) {
      return menuRepository.findSimpleMenusByTerminal(terminal)
    }
    val platformDo = platformRepository.findByCode(platform) ?: let {
      log.error("{}获取平台: {} 信息返回空", logPrefix, platform)
      throw ResourceNotFoundException("平台不存在")
    }
    if (platformDo.isTenantHasAllMenus) {
      return menuRepository.findSimpleMenusByTerminal(terminal)
    }
    val roleDo = roleRepository.findTenantAdmin(platform, tenantId) ?: let {
      log.info("{}获取租户超管信息返回空: [{} {}]", logPrefix, platform, tenantId)
      throw ResourceNotFoundException("无法获取当前租户的超管信息")
    }
    val menuIds = roleMenuRelRepository
      .findAllByRoleIdIn(listOf(roleDo.id))
      .mapTo(HashSet()) { it.menuId }
      .ifEmpty { null } ?: return emptyList()
    return menuRepository.findSimpleMenusById(menuIds)
  }

  /** 获取角色下的菜单列表 */
  suspend fun menus(id: Long, platform: String? = null, tenantId: Long? = null): List<SimpleMenu> {
    val roleDo = checkAndGetRole(id, platform, tenantId)
    val menuIds = roleMenuRelRepository
      .findAllByRoleIdIn(listOf(roleDo.id))
      .mapTo(HashSet()) { it.menuId }
      .ifEmpty { null } ?: return emptyList()
    return menuRepository.findSimpleMenusById(menuIds)
  }

  /** 为角色分配菜单 */
  suspend fun assignMenus(args: AssignMenuArgs, platform: String? = null, tenantId: Long? = null) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val roleId = args.roleId.requireNonnull { "角色id为空" }
    val terminal = args.terminal.requireNotBlank { "终端编码为空" }
    val menus = args.menus?.toSet()
    val roleDo = checkAndGetRole(roleId, platform, tenantId)
    Operations.details("变更[${roleDo.name}]的菜单权限")
    val count = roleMenuRelRepository.deleteAllByRoleIdAndTerminal(roleId, terminal)
    log.info("{}移除角色: {} 在终端: {} 下菜单关系 {}条", logPrefix, roleId, terminal, count)
    if (menus.isNullOrEmpty()) {
      return
    }
    val assignableMenus = assignableMenus(roleDo.platform, tenantId, terminal)
    val assignableMenuIds = assignableMenus.mapTo(HashSet()) { it.id }
    if (!assignableMenuIds.containsAll(menus)) {
      log.info("{}分配菜单失败, 因为没有部分菜单的操作权限", logPrefix)
      throw ForbiddenException("缺少部分菜单的操作权限")
    }
    val menuRelDos = menus.map { RoleMenuRelDo.create(roleId, terminal, it) }
    roleMenuRelRepository.insertAll(menuRelDos)
    log.info(
      "{}分配角色: {} 在终端: {} 下菜单关系 {}条", logPrefix, roleId, terminal, menuRelDos.size
    )
  }

  private suspend fun checkAndGetRole(
    roleId: Long,
    platform: String?,
    tenantId: Long?
  ): RoleDo {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    return roleRepository.findById(roleId)?.also {
      if (platform != null && platform != it.platform) {
        throw ForbiddenException("没有此角色的管理权限")
      }
      if (tenantId != null && tenantId != it.tenantId) {
        throw ForbiddenException("没有此角色的管理权限")
      }
    } ?: let {
      log.info("{}角色: {} 不存在", logPrefix, roleId)
      throw ResourceNotFoundException("角色不存在")
    }
  }
}
