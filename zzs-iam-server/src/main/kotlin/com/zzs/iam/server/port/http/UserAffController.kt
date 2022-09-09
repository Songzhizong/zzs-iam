package com.zzs.iam.server.port.http

import com.zzs.framework.core.spring.toPageResult
import com.zzs.framework.core.transmission.ListResult
import com.zzs.framework.core.transmission.PageResult
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNonnull
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.framework.core.utils.requireNotEmpty
import com.zzs.iam.common.infrastructure.security.SecurityContextHolder
import com.zzs.iam.common.pojo.JoinedUser
import com.zzs.iam.server.application.UserAffService
import com.zzs.iam.server.domain.model.org.PlatformUserRepository
import com.zzs.iam.server.dto.args.AddUserArgs
import com.zzs.iam.server.dto.args.ChangeUserRoleArgs
import com.zzs.iam.server.dto.args.QueryUserArgs
import com.zzs.iam.server.dto.args.UserIdsArgs
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 用户归属管理
 *
 * @author 宋志宗 on 2022/8/18
 */
@RestController
@RequestMapping("/iam/user_aff")
class UserAffController(
  private val userAffService: UserAffService,
  private val platformUserRepository: PlatformUserRepository,
) {

  /**
   * 向指定平台添加用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user_aff/platform/add_user?platform=iam
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "userIds": [
   *       "393023645141696512"
   *     ],
   *     "roles": [
   *     ]
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": [
   *       {
   *         "id": "393026234268778496",
   *         "platform": "iam",
   *         "userId": "393023645141696512",
   *         "name": "张三",
   *         "nickname": "",
   *         "account": "zhangsan",
   *         "phone": "18866666666",
   *         "email": "zhangsan@163.com",
   *         "createdTime": "2022-08-22 22:52:34.213",
   *         "updatedTime": "2022-08-22 22:52:34.213"
   *       }
   *     ]
   *   }
   * </pre>
   *
   * @param platform 平台编码
   */
  @Suppress("DuplicatedCode")
  @PostMapping("/platform/add_user")
  suspend fun platformAddUser(
    platform: String?,
    @RequestBody args: AddUserArgs
  ): ListResult<JoinedUser> {
    platform.requireNotBlank { "平台编码为空" }
    val userIds = args.userIds.requireNotEmpty { "用户ID为空" }
    val roles = args.roles
    val users = userAffService.addPlatformUser(platform, userIds.toSet(), roles?.toSet())
    val map = users.map { it.toJoinedUser().desensitize() }
    return ListResult.of(map)
  }

  /**
   * 从指定平台移除用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user_aff/platform/remove_user?platform=iam
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "userIds": [
   *       "393023645141696512"
   *     ]
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   *
   * @param platform 平台编码
   */
  @Suppress("DuplicatedCode")
  @PostMapping("/platform/remove_user")
  suspend fun platformRemoveUser(
    platform: String?,
    @RequestBody args: UserIdsArgs
  ): Result<Void> {
    platform.requireNotBlank { "平台编码为空" }
    val userIds = args.userIds.requireNotEmpty { "用户ID列表为空" }
    userAffService.removePlatformUsers(platform, userIds.toSet())
    return Result.success()
  }

  /**
   * 冻结指定平台下用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user_aff/platform/freeze_user?platform=iam
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "userIds": [
   *       "393023720915992576",
   *       "393023645141696512"
   *     ]
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   */
  @PostMapping("/platform/freeze_user")
  suspend fun platformFreezeUser(platform: String?, @RequestBody args: UserIdsArgs): Result<Void> {
    platform.requireNotBlank { "平台编码为空" }
    val userIds = args.userIds.requireNotEmpty { "用户列表为空" }
    userAffService.freezePlatformUser(platform, userIds.toSet())
    return Result.success()
  }

  /**
   * 解冻指定平台下用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user_aff/platform/unfreeze_user?platform=iam
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "userIds": [
   *       "393023720915992576",
   *       "393023645141696512"
   *     ]
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   */
  @PostMapping("/platform/unfreeze_user")
  suspend fun platformUnfreezeUser(
    platform: String?,
    @RequestBody args: UserIdsArgs
  ): Result<Void> {
    platform.requireNotBlank { "平台编码为空" }
    val userIds = args.userIds.requireNotEmpty { "用户列表为空" }
    userAffService.unfreezePlatformUser(platform, userIds.toSet())
    return Result.success()
  }

  /**
   * 查询指定平台下的用户列表
   * <pre>
   *   <p><b>请求示例</b></p>
   *   关键数据脱敏
   *   POST {{base_url}}/iam/user_aff/platform/query_user?platform=iam
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "paging": {
   *       "pageNumber": 1,
   *       "pageSize": 10
   *     },
   *     "uniqueIdent": "general@163.com"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": [
   *       {
   *         "id": "393023954303844352",
   *         "platform": "iam",
   *         "userId": "393023720915992576",
   *         "name": "宋某某",
   *         "nickname": "",
   *         "account": "general",
   *         "phone": "18888888888",
   *         "email": "general@163.com",
   *         "createdTime": "2022-08-22 22:43:30.627",
   *         "updatedTime": "2022-08-22 22:43:30.627"
   *       }
   *     ],
   *     "page": 1,
   *     "size": 10,
   *     "total": "1",
   *     "totalPages": 1
   *   }
   * </pre>
   *
   * @param platform 平台编码
   */
  @PostMapping("/platform/query_user")
  suspend fun queryPlatformUser(
    platform: String?,
    @RequestBody args: QueryUserArgs
  ): PageResult<JoinedUser> {
    platform.requireNotBlank { "平台编码为空" }
    val page = platformUserRepository.query(platform, args)
    return page.toPageResult { it.toJoinedUser().desensitize() }
  }

  /**
   * 向指定租户下添加用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/tenant/add_user")
  suspend fun tenantAddUser(
    tenantId: Long?,
    @RequestBody args: AddUserArgs
  ): ListResult<JoinedUser> {
    tenantId.requireNonnull { "租户ID为空" }
    val userIds = args.userIds.requireNotEmpty { "用户ID为空" }
    val roles = args.roles
    val users = userAffService.addTenantUser(tenantId, userIds.toSet(), roles?.toSet())
    val map = users.map { it.toJoinedUser().desensitize() }
    return ListResult.of(map)
  }

  /**
   * 从指定租户移除用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/tenant/remove_user")
  suspend fun tenantRemoveUser(tenantId: Long?, @RequestBody args: UserIdsArgs): Result<Void> {
    tenantId.requireNonnull { "租户ID为空" }
    val userIds = args.userIds.requireNotEmpty { "用户列表为空" }
    userAffService.removeTenantUsers(tenantId, userIds.toSet())
    return Result.success()
  }

  /**
   * 冻结指定租户下的用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/tenant/freeze_user")
  suspend fun tenantFreezeUser(tenantId: Long?, @RequestBody args: UserIdsArgs): Result<Void> {
    tenantId.requireNonnull { "租户ID为空" }
    val userIds = args.userIds.requireNotEmpty { "用户列表为空" }
    userAffService.freezeTenantUser(tenantId, userIds.toSet())
    return Result.success()
  }

  /**
   * 解冻指定租户下的用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/tenant/unfreeze_user")
  suspend fun tenantUnfreezeUser(tenantId: Long?, @RequestBody args: UserIdsArgs): Result<Void> {
    tenantId.requireNonnull { "租户ID为空" }
    val userIds = args.userIds.requireNotEmpty { "用户列表为空" }
    userAffService.unfreezeTenantUser(tenantId, userIds.toSet())
    return Result.success()
  }

  /**
   * 向当前平台(租户)添加用户
   * <pre>
   *   如果当前是多租户平台, 则向当前所在租户添加用户;
   *   如果当前非多租户平台, 则向当前所在平台添加用户
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/cur/add_user")
  suspend fun addUser(@RequestBody args: AddUserArgs): ListResult<JoinedUser> {
    val context = SecurityContextHolder.current()
    val multiTenant = context.isMultiTenant
    if (multiTenant) {
      val tenantId = context.possibleTenantId()
      return tenantAddUser(tenantId, args)
    }
    val platform = context.platform
    return platformAddUser(platform, args)
  }

  /**
   * 移除当前平台(租户)下的用户
   * <pre>
   *   如果当前是多租户平台, 则移除当前租户下的指定用户;
   *   如果当前非多租户平台, 则移除当前平台下的指定用户
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/cur/remove_user")
  suspend fun removeUser(@RequestBody args: UserIdsArgs): Result<Void> {
    val context = SecurityContextHolder.current()
    val multiTenant = context.isMultiTenant
    if (multiTenant) {
      val tenantId = context.possibleTenantId()
      return tenantRemoveUser(tenantId, args)
    }
    val platform = context.platform
    return platformRemoveUser(platform, args)
  }

  /**
   * 冻结当前平台(租户)下的用户
   * <pre>
   *   如果当前是多租户平台, 则冻结当前租户下的指定用户;
   *   如果当前非多租户平台, 则冻结当前平台下的指定用户
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/cur/freeze_user")
  suspend fun freezeUser(@RequestBody args: UserIdsArgs): Result<Void> {
    val context = SecurityContextHolder.current()
    val multiTenant = context.isMultiTenant
    if (multiTenant) {
      val tenantId = context.possibleTenantId()
      return tenantFreezeUser(tenantId, args)
    }
    val platform = context.platform
    return platformFreezeUser(platform, args)
  }

  /**
   * 解冻当前平台(租户)下的用户
   * <pre>
   *   如果当前是多租户平台, 则解冻当前租户下的指定用户;
   *   如果当前非多租户平台, 则解冻当前平台下的指定用户
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/cur/unfreeze_user")
  suspend fun unfreezeUser(@RequestBody args: UserIdsArgs): Result<Void> {
    val context = SecurityContextHolder.current()
    val multiTenant = context.isMultiTenant
    if (multiTenant) {
      val tenantId = context.possibleTenantId()
      return tenantUnfreezeUser(tenantId, args)
    }
    val platform = context.platform
    return platformUnfreezeUser(platform, args)
  }

  /**
   * 修改当前平台(租户)下指定用户的角色
   *   如果当前是多租户平台, 则修改当前所在租户下指定用户的角色;
   *   如果当前非多租户平台, 则修改当前所在平台下指定用户的角色
   * <pre>
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/cur/change_roles")
  suspend fun changeRoles(@RequestBody args: ChangeUserRoleArgs): Result<Void> {
    val userIds = args.userIds.requireNotEmpty { "用户ID为空" }
    val roles = args.roles
    val context = SecurityContextHolder.current()
    val platform = context.platform
    val tenantId = context.possibleTenantId()
    userAffService.changeRoles(platform, tenantId, userIds.toSet(), roles?.toSet() ?: emptySet())
    return Result.success()
  }

  /**
   * 查询当前平台(租户)下的用户列表
   * <pre>
   *   如果当前是多租户平台, 则查询当前所在租户下的用户列表;
   *   如果当前非多租户平台, 则查询当前平台下的用户列表
   *   <p><b>请求示例</b></p>
   *   <p><b>响应示例</b></p>
   * </pre>
   */
  @PostMapping("/cur/query_user")
  suspend fun queryUser(@RequestBody args: QueryUserArgs): PageResult<JoinedUser> {
    val current = SecurityContextHolder.current()
    val platform = current.platform
    val multiTenant = current.isMultiTenant
    if (!multiTenant) {
      return queryPlatformUser(platform, args)
    }
    return PageResult.empty(args.paging)
  }
}
