package com.zzs.iam.upms.port.http

import cn.idealframework2.trace.Operation
import cn.idealframework2.transmission.ListResult
import cn.idealframework2.transmission.Result
import cn.idealframework2.utils.requireNonnull
import cn.idealframework2.utils.requireNotBlank
import cn.idealframework2.utils.toTreeList
import com.zzs.iam.common.pojo.Role
import com.zzs.iam.common.pojo.SimpleMenu
import com.zzs.iam.upms.application.RoleService
import com.zzs.iam.upms.domain.model.role.RoleRepository
import com.zzs.iam.upms.dto.args.AssignMenuArgs
import com.zzs.iam.upms.dto.args.RoleArgs
import com.zzs.iam.upms.dto.resp.SimpleMenuTree
import com.zzs.iam.upms.infrastructure.security.SecurityContextHolder
import org.springframework.web.bind.annotation.*

/**
 * 角色管理
 *
 * @author 宋志宗 on 2022/8/16
 */
@RestController
@RequestMapping("/iam/menu")
class RoleController(
  private val roleService: RoleService,
  private val roleRepository: RoleRepository,
) {

  /**
   * 为当前平台/租户创建角色
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @Operation("新增角色")
  @PostMapping("/cur/create_role")
  suspend fun create(@RequestBody args: RoleArgs): Result<Role> {
    val current = SecurityContextHolder.current()
    val platform = current.platform
    val tenantId = current.possibleTenantId()
    val name = args.name.requireNotBlank { "角色名称为空" }
    val note = args.note
    val roleDo = roleService.createGeneral(platform, tenantId, name, note)
    val role = roleDo.toRole()
    return Result.success(role)
  }

  /**
   * 更新角色信息
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   *
   * @param id 角色id
   */
  @Operation("修改角色信息")
  @PostMapping("/update_role")
  suspend fun update(id: Long?, @RequestBody args: RoleArgs): Result<Role> {
    id.requireNonnull { "角色id为空" }
    val name = args.name.requireNotBlank { "角色名称为空" }
    val note = args.note
    val context = SecurityContextHolder.optional()
    val roleDo = if (context == null) {
      roleService.update(id, name, note)
    } else {
      val platform = context.platform
      val tenantId = context.possibleTenantId()
      roleService.update(id, name, note, platform, tenantId)
    }
    val role = roleDo.toRole()
    return Result.success(role)
  }

  /**
   * 将角色设置为基础角色
   * <pre>
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   *
   * @param id 角色id
   */
  @Operation("设置基础角色")
  @PostMapping("/set_as_basic")
  suspend fun setAsBasic(id: Long?): Result<Void> {
    id.requireNonnull { "角色id为空" }
    val context = SecurityContextHolder.optional()
    if (context == null) {
      roleService.setAsBasic(id)
    } else {
      val platform = context.platform
      val tenantId = context.possibleTenantId()
      roleService.setAsBasic(id, platform, tenantId)
    }
    return Result.success()
  }

  /**
   * 取消基础角色
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   *
   * @param id 角色id
   */
  @Operation("取消基础角色")
  @PostMapping("/cancel_basic")
  suspend fun cancelBasic(id: Long?): Result<Void> {
    id.requireNonnull { "角色id为空" }
    val context = SecurityContextHolder.optional()
    if (context == null) {
      roleService.cancelBasic(id)
    } else {
      val platform = context.platform
      val tenantId = context.possibleTenantId()
      roleService.cancelBasic(id, platform, tenantId)
    }
    return Result.success()
  }

  /**
   * 删除角色
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   *
   * @param id 角色id
   */
  @Operation("删除角色")
  @PostMapping("/delete_role")
  suspend fun delete(id: Long?): Result<Void> {
    id.requireNonnull { "角色id为空" }
    val context = SecurityContextHolder.optional()
    if (context == null) {
      roleService.delete(id)
    } else {
      val platform = context.platform
      val tenantId = context.possibleTenantId()
      roleService.delete(id, platform, tenantId)
    }
    return Result.success()
  }

  /**
   * 获取当前平台(租户)下所有角色
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @GetMapping("/cur/get_all")
  suspend fun getAll(): ListResult<Role> {
    val context = SecurityContextHolder.current()
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    val list = roleRepository.findAll(platform, tenantId)
    val roles = list.map { it.toRole() }
    return ListResult.of(roles)
  }

  /**
   * 获取指定终端下可分配的菜单树
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   *
   * @param terminal 终端编码
   */
  @GetMapping("/cur/assignable_menus")
  suspend fun availableMenus(terminal: String?): ListResult<SimpleMenuTree> {
    terminal.requireNotBlank { "终端编码不能为空" }
    val context = SecurityContextHolder.current()
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    val menus = roleService.assignableMenus(platform, tenantId, terminal)
    val treeList = menus.map { SimpleMenuTree.of(it) }.toTreeList()
    return ListResult.of(treeList)
  }

  /**
   * 获取指定角色的菜单列表
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   *
   * @param id 角色id
   */
  @GetMapping("/menus")
  suspend fun menus(id: Long?): ListResult<SimpleMenu> {
    id.requireNonnull { "角色id为空" }
    val context = SecurityContextHolder.optional()
    val menus = if (context == null) {
      roleService.menus(id)
    } else {
      val platform = context.platform
      val tenantId = context.possibleTenantId()
      roleService.menus(id, platform, tenantId)
    }
    return ListResult.of(menus)
  }

  /**
   * 为角色分配菜单
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @Operation("为角色分配菜单")
  @PostMapping("/assign_menus")
  suspend fun assignMenus(@RequestBody args: AssignMenuArgs): Result<Void> {
    val context = SecurityContextHolder.optional()
    if (context == null) {
      roleService.assignMenus(args)
    } else {
      val platform = context.platform
      val tenantId = context.possibleTenantId()
      roleService.assignMenus(args, platform, tenantId)
    }
    return Result.success()
  }

}
