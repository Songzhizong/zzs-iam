package com.zzs.iam.upms.port.http

import cn.idealframework2.trace.Operation
import cn.idealframework2.transmission.Result
import cn.idealframework2.utils.requireNonnull
import cn.idealframework2.utils.requireNotBlank
import com.zzs.iam.common.pojo.User
import com.zzs.iam.upms.application.UserService
import com.zzs.iam.upms.dto.args.RegisterUserArgs
import com.zzs.iam.upms.dto.args.UpdateUserArgs
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

/**
 * IAM用户数据库
 *
 * @author 宋志宗 on 2022/8/15
 */
@RestController
@RequestMapping("/iam/user")
class UserController(
  private val userService: UserService,
) {

  /**
   * 用户注册
   * <pre>
   *   不需要鉴权
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/register
   *   Content-Type: application/json
   *
   *
   *   {
   *     "name": "张三",
   *     "account": "zhangsan",
   *     "phone": "18888888888",
   *     "email": "zhangsan@163.com",
   *     "password": "geQHJF9fke2gGTFihnhuRoRBfphPWYM7"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "391967965454532608",
   *       "name": "张三",
   *       "nickname": "",
   *       "account": "zhangsan",
   *       "phone": "18888888888",
   *       "email": "zhangsan@163.com",
   *       "profilePhoto": "",
   *       "frozen": false,
   *       "passwordDate": "2022-08-20",
   *       "createdTime": "2022-08-20 00:47:23.266",
   *       "updatedTime": "2022-08-20 00:47:23.266"
   *     }
   *   }
   * </pre>
   */
  @PostMapping("/register")
  suspend fun register(@RequestBody args: RegisterUserArgs): Result<User> {
    val userDo = userService.register(args)
    val user = userDo.toUser()
    return Result.success(user)
  }

  /**
   * 冻结用户
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/freeze?id=391954220057624576
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   */
  @Operation("冻结用户")
  @PostMapping("/freeze")
  suspend fun freeze(id: Long?): Result<Void> {
    id.requireNonnull { "用户id为空" }
    userService.freeze(id)
    return Result.success()
  }

  /**
   * 解除冻结
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/unfreeze?id=391954220057624576
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   */
  @Operation("解冻用户")
  @PostMapping("/unfreeze")
  suspend fun unfreeze(id: Long?): Result<Void> {
    id.requireNonnull { "用户id为空" }
    userService.unfreeze(id)
    return Result.success()
  }

  /**
   * 通过主键获取用户信息
   * <pre>
   *   <p><b>请求示例</b></p>
   *   GET {{base_url}}/iam/user/get_user_by_id?id=391954220057624576
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "391954220057624576",
   *       "name": "宋某某",
   *       "nickname": "",
   *       "account": "general",
   *       "phone": "18666666666",
   *       "email": "general@126.com",
   *       "profilePhoto": "",
   *       "frozen": true,
   *       "passwordDate": "2022-08-20",
   *       "createdTime": "2022-08-19 23:52:46.108",
   *       "updatedTime": "2022-08-20 00:48:24.915"
   *     }
   *   }
   * </pre>
   */
  @GetMapping("/get_user_by_id")
  suspend fun getById(id: Long?): Result<User> {
    id.requireNonnull { "用户id为空" }
    val user = userService.getById(id)
    return Result.success(user)
  }

  /**
   * 选择性更新用户信息
   * <pre>
   *   为null的字段不更新
   *   <b>请求示例</b>
   *   POST {{base_url}}/iam/user/selectivity_update?id=391954220057624576
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "name": null,
   *     "nickname": "宋某某",
   *     "account": null,
   *     "phone": null,
   *     "email": null,
   *     "profilePhoto": null
   *   }
   *   <b>响应示例</b>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "391954220057624576",
   *       "name": "宋某某",
   *       "nickname": "宋某某",
   *       "account": "general",
   *       "phone": "18666666666",
   *       "email": "general@126.com",
   *       "profilePhoto": "",
   *       "frozen": true,
   *       "passwordDate": "2022-08-20",
   *       "createdTime": "2022-08-19 23:52:46.108",
   *       "updatedTime": "2022-08-20 00:51:32.022"
   *     }
   *   }
   * </pre>
   */
  @Operation("更新用户信息")
  @PostMapping("/selectivity_update")
  suspend fun selectivityUpdate(id: Long?, @RequestBody args: UpdateUserArgs): Result<User> {
    id.requireNonnull { "用户id为空" }
    val userDo = userService.selectivityUpdate(id, args)
    val user = userDo.toUser()
    return Result.success(user)
  }

  /**
   * 通过用户身份标识和密码进行身份认证
   *
   * @param uniqueIdent 账号/邮箱/手机号
   * @param password    密码
   * @ignore
   */
  @PostMapping("/authenticate/password")
  suspend fun authenticate(
    uniqueIdent: String?,
    password: String?,
    exchange: ServerWebExchange
  ): Result<User> {
    val formData = exchange.formData.awaitSingleOrNull()
    val userDo = userService.authenticate(
      formData?.getFirst("uniqueIdent")?.ifBlank { null }
        ?: uniqueIdent.requireNotBlank { "用户标识为空" },
      formData?.getFirst("password")?.ifBlank { null }
        ?: password.requireNotBlank { "密码" },
    )
    val user = userDo.toUser()
    return Result.success(user)
  }
}
