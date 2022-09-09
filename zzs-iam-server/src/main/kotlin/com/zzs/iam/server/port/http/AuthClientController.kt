package com.zzs.iam.server.port.http

import com.zzs.framework.core.transmission.ListResult
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.pojo.AuthClient
import com.zzs.iam.server.application.AuthClientService
import com.zzs.iam.server.domain.model.org.AuthClientRepository
import com.zzs.iam.server.dto.args.CreateAuthClientArgs
import org.springframework.web.bind.annotation.*

/**
 * 认证客户端管理
 *
 * @author 宋志宗 on 2022/8/15
 */
@RestController
@RequestMapping("/iam/auth_client")
class AuthClientController(
  private val authClientService: AuthClientService,
  private val authClientRepository: AuthClientRepository,
) {

  /**
   * 新增客户端
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/auth_client/create_client
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "platform": "iam",
   *     "clientId": "iam_wechat",
   *     "clientSecret": "iam_wechat",
   *     "name": "iam_wechat",
   *     "note": null,
   *     "accessTokenValidity": null,
   *     "refreshTokenValidity": null,
   *     "accessTokenAutoRenewal": true
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "platform": "iam",
   *       "multiTenant": false,
   *       "clientId": "iam_wechat",
   *       "name": "iam_wechat",
   *       "accessTokenValidity": 3600,
   *       "refreshTokenValidity": 1296000,
   *       "accessTokenAutoRenewal": true,
   *       "createdTime": "2022-08-20 00:59:54.147",
   *       "updatedTime": "2022-08-20 00:59:54.147"
   *     }
   *   }
   * </pre>
   */
  @PostMapping("/create_client")
  suspend fun create(@RequestBody args: CreateAuthClientArgs): Result<AuthClient> {
    val clientDo = authClientService.create(args)
    val client = clientDo.toAuthClient()
    return Result.success(client)
  }

  /**
   * 删除客户端
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/auth_client/delete_client?clientId=iam_wechat
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   *
   * @param clientId 客户端唯一ID
   */
  @PostMapping("/delete_client")
  suspend fun delete(clientId: String?): Result<Void> {
    clientId.requireNotBlank { "客户端ID为空" }
    authClientService.delete(clientId)
    return Result.success()
  }

  /**
   * 获取平台下所有的认证客户端
   * <pre>
   *   <p><b>请求示例</b></p>
   *   GET {{base_url}}/iam/auth_client/find_all_by_platform?platform=iam
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": [
   *       {
   *         "platform": "iam",
   *         "multiTenant": false,
   *         "clientId": "iam_web",
   *         "name": "iam_web",
   *         "accessTokenValidity": 3600,
   *         "refreshTokenValidity": 1296000,
   *         "accessTokenAutoRenewal": true,
   *         "createdTime": "2022-08-19 23:54:08.209",
   *         "updatedTime": "2022-08-19 23:54:08.209"
   *       },
   *       {
   *         "platform": "iam",
   *         "multiTenant": false,
   *         "clientId": "iam_wechat",
   *         "name": "iam_wechat",
   *         "accessTokenValidity": 3600,
   *         "refreshTokenValidity": 1296000,
   *         "accessTokenAutoRenewal": true,
   *         "createdTime": "2022-08-20 00:59:54.147",
   *         "updatedTime": "2022-08-20 00:59:54.147"
   *       }
   *     ]
   *   }
   * </pre>
   *
   * @param platform 平台编码
   */
  @GetMapping("/find_all_by_platform")
  suspend fun findAllByPlatform(platform: String?): ListResult<AuthClient> {
    platform.requireNotBlank { "平台编码为空" }
    val doList = authClientRepository.findAllByPlatform(platform)
    val list = doList.map { it.toAuthClient() }
    return ListResult.of(list)
  }
}
