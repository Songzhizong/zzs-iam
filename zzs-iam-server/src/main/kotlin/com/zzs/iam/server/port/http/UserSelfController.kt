package com.zzs.iam.server.port.http

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.infrastructure.security.SecurityContextHolder
import com.zzs.iam.common.pojo.User
import com.zzs.iam.server.application.UserService
import com.zzs.iam.server.dto.args.ChangeAccountArgs
import com.zzs.iam.server.dto.args.ChangeEmailArgs
import com.zzs.iam.server.dto.args.ChangePasswordArgs
import com.zzs.iam.server.dto.args.ChangePhoneArgs
import org.springframework.web.bind.annotation.*

/**
 * 当前登录用户自身管理接口
 *
 * @author 宋志宗 on 2022/8/15
 */
@RestController
@RequestMapping("/iam/user/self")
class UserSelfController(
  private val userService: UserService,
) {

  /**
   * 获取当前登录用户的个人信息
   * <pre>
   *   不需要api鉴权
   *   <p><b>请求示例</b></p>
   *   GET {{base_url}}/iam/user/self/information
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "391954220057624576",
   *       "name": "宋某某",
   *       "nickname": "",
   *       "account": "ge***al",
   *       "phone": "188****8888",
   *       "email": "gene***@163.com",
   *       "profilePhoto": "",
   *       "frozen": false,
   *       "passwordDate": "2022-08-19",
   *       "createdTime": "2022-08-19 23:52:46.108",
   *       "updatedTime": "2022-08-19 23:52:46.108"
   *     }
   *   }
   * </pre>
   */
  @GetMapping("/information")
  suspend fun currentUser(): Result<User> {
    val context = SecurityContextHolder.current()
    val userId = context.userId.toLong()
    val user = userService.getById(userId).desensitize()
    return Result.success(user)
  }

  /**
   * 修改当前登录用户密码
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/self/change_password
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "oldPassword": "geQHJF9fke2gGTFihnhuRoRBfphPWYM7",
   *     "newPassword": "geQHJF9fke2gGTFihnhuRoRBfphPWYM",
   *     "confirmationPassword": "geQHJF9fke2gGTFihnhuRoRBfphPWYM"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *    }
   * </pre>
   */
  @PostMapping("/change_password")
  suspend fun changePassword(@RequestBody args: ChangePasswordArgs): Result<Void> {
    val oldPassword = args.oldPassword.requireNotBlank { "原密码为空" }
    val newPassword = args.newPassword.requireNotBlank { "新密码为空" }
    val confirmationPassword = args.confirmationPassword.requireNotBlank { "确认密码为空" }
    if (oldPassword == newPassword) {
      throw BadRequestException("原密码与新密码一致")
    }
    if (newPassword != confirmationPassword) {
      throw BadRequestException("新密码与确认密码不一致")
    }
    val context = SecurityContextHolder.current()
    val userId = context.userId.toLong()
    userService.changePassword(userId, newPassword, oldPassword)
    return Result.success()
  }


  /**
   * 修改当前登录用户手机号
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/self/change_account
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "password": "geQHJF9fke2gGTFihnhuRoRBfphPWYM",
   *     "newAccount": "new_account"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *    }
   * </pre>
   */
  @PostMapping("/change_account")
  suspend fun changeAccount(@RequestBody args: ChangeAccountArgs): Result<Void> {
    val password = args.password.requireNotBlank { "密码为空" }
    val newAccount = args.newAccount.requireNotBlank { "新账号为空" }
    val context = SecurityContextHolder.current()
    val userId = context.userId.toLong()
    userService.changeAccount(userId, newAccount, password)
    return Result.success()
  }

  /**
   * 修改当前登录用户手机号
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/self/change_phone
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "password": "geQHJF9fke2gGTFihnhuRoRBfphPWYM",
   *     "newPhone": "18666666666"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *    }
   * </pre>
   */
  @PostMapping("/change_phone")
  suspend fun changePhone(@RequestBody args: ChangePhoneArgs): Result<Void> {
    val password = args.password.requireNotBlank { "密码为空" }
    val newPhone = args.newPhone.requireNotBlank { "新手机号码为空" }
    val context = SecurityContextHolder.current()
    val userId = context.userId.toLong()
    userService.changePhone(userId, newPhone, password)
    return Result.success()
  }

  /**
   * 修改当前登录用户邮箱
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/user/self/change_email
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "password": "geQHJF9fke2gGTFihnhuRoRBfphPWYM",
   *     "newEmail": "general@126.com"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *    }
   * </pre>
   */
  @PostMapping("/change_email")
  suspend fun changeEmail(@RequestBody args: ChangeEmailArgs): Result<Void> {
    val password = args.password.requireNotBlank { "密码为空" }
    val newEmail = args.newEmail.requireNotBlank { "邮箱为空" }
    val context = SecurityContextHolder.current()
    val userId = context.userId.toLong()
    userService.changeEmail(userId, newEmail, password)
    return Result.success()
  }
}
