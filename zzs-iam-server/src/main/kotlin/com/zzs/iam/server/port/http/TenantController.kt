package com.zzs.iam.server.port.http

import com.zzs.framework.core.spring.toPageResult
import com.zzs.framework.core.trace.Operation
import com.zzs.framework.core.transmission.ListResult
import com.zzs.framework.core.transmission.PageResult
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNonnull
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.framework.core.utils.toTreeList
import com.zzs.iam.common.pojo.Tenant
import com.zzs.iam.server.application.TenantService
import com.zzs.iam.server.domain.model.org.TenantRepository
import com.zzs.iam.server.dto.args.CreateTenantArgs
import com.zzs.iam.server.dto.args.QueryTenantArgs
import com.zzs.iam.server.dto.resp.TenantTree
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 租户管理
 *
 * @author 宋志宗 on 2022/8/19
 */
@RestController
@RequestMapping("/iam/tenant")
class TenantController(
  private val tenantService: TenantService,
  private val tenantRepository: TenantRepository,
) {

  /**
   * 新增租户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @Operation("新增租户")
  @PostMapping("/create_tenant")
  suspend fun create(platform: String?, args: CreateTenantArgs): Result<Tenant> {
    platform.requireNotBlank { "平台编码为空" }
    val tenantDo = tenantService.create(platform, args)
    val tenant = tenantDo.toTenant()
    return Result.success(tenant)
  }

  /**
   * 冻结租户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @Operation("冻结租户")
  @PostMapping("/freeze_tenant")
  suspend fun freeze(id: Long?): Result<Void> {
    id.requireNonnull { "租户id为空" }
    tenantService.freeze(id)
    return Result.success()
  }

  /**
   * 解除冻结
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @Operation("解冻租户")
  @PostMapping("/unfreeze_tenant")
  suspend fun unfreeze(id: Long?): Result<Void> {
    id.requireNonnull { "租户id为空" }
    tenantService.unfreeze(id)
    return Result.success()
  }

  /**
   * 删除租户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @Operation("删除租户")
  @PostMapping("/delete")
  suspend fun delete(id: Long?): Result<Void> {
    id.requireNonnull { "租户id为空" }
    tenantService.delete(id)
    return Result.success()
  }

  /**
   * 查询租户列表
   * <pre>
   *   如果没有查询条件则只查询顶级租户
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   */
  @PostMapping("/query_tenant")
  suspend fun query(platform: String?, args: QueryTenantArgs): PageResult<Tenant> {
    platform.requireNotBlank { "平台编码为空" }
    val page = tenantRepository.query(platform, args)
    return page.toPageResult { it.toTenant() }
  }

  /**
   * 获取指定租户下的子租户树
   * <pre>
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   */
  @GetMapping("/child_tree")
  suspend fun childTree(id: Long?): ListResult<TenantTree> {
    id.requireNonnull { "租户id为空" }
    val childList = tenantService.getAllChild(id)
    val trees = childList.map { TenantTree.of(it) }.toTreeList()
    return ListResult.of(trees)
  }

  /**
   * 获取指定租户下所有的子租户
   * <pre>
   *   <b>请求示例</b>
   *   <b>响应示例</b>
   * </pre>
   */
  @GetMapping("/child_list")
  suspend fun childList(id: Long?): ListResult<Tenant> {
    id.requireNonnull { "租户id为空" }
    val childList = tenantService.getAllChild(id)
    val tenants = childList.map { it.toTenant() }
    return ListResult.of(tenants)
  }
}
