package com.zzs.iam.upms.port.http

import cn.idealframework2.trace.Operation
import cn.idealframework2.transmission.ListResult
import cn.idealframework2.transmission.Result
import cn.idealframework2.utils.requireNotBlank
import com.zzs.iam.common.pojo.Platform
import com.zzs.iam.upms.application.PlatformService
import com.zzs.iam.upms.domain.model.org.PlatformRepository
import com.zzs.iam.upms.dto.args.CreatePlatformArgs
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 平台管理
 *
 * @author 宋志宗 on 2022/8/15
 */
@RestController
@RequestMapping("/iam/platform")
class PlatformController(
  private val platformService: PlatformService,
  private val platformRepository: PlatformRepository
) {

  /**
   * 新增平台
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST http://127.0.0.1:30030/iam/platform/create_platform
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "code": "dcim",
   *     "name": "dcim",
   *     "multiTenant": true,
   *     "enableApiAuthenticate": false
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "code": "dcim",
   *       "name": "dcim",
   *       "multiTenant": true,
   *       "enableApiAuthenticate": false,
   *       "createdTime": "2022-08-20 00:54:17.213",
   *       "updatedTime": "2022-08-20 00:54:17.213"
   *     }
   *   }
   * </pre>
   */
  @Operation("新增平台")
  @PostMapping("/create_platform")
  suspend fun create(@RequestBody args: CreatePlatformArgs): Result<Platform> {
    val platformDo = platformService.create(args)
    val platform = platformDo.toPlatform()
    return Result.success(platform)
  }

  /**
   * 删除平台
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/platform/delete_platform?code=dcim
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   *
   * @param code 平台编码
   */
  @Operation("删除平台")
  @PostMapping("/delete_platform")
  suspend fun delete(code: String?): Result<Void> {
    code.requireNotBlank { "平台编码为空" }
    platformService.delete(code)
    return Result.success()
  }

  /**
   * 获取所有平台
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/platform/find_all
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": [
   *       {
   *         "code": "iam",
   *         "name": "iam",
   *         "multiTenant": false,
   *         "enableApiAuthenticate": false,
   *         "createdTime": "2022-08-19 23:53:04.452",
   *         "updatedTime": "2022-08-19 23:53:04.452"
   *       },
   *       {
   *         "code": "dcim",
   *         "name": "dcim",
   *         "multiTenant": true,
   *         "enableApiAuthenticate": false,
   *         "createdTime": "2022-08-20 00:54:17.213",
   *         "updatedTime": "2022-08-20 00:54:17.213"
   *       }
   *     ]
   *   }
   * </pre>
   */
  @PostMapping("/find_all")
  suspend fun findAll(): ListResult<Platform> {
    val doList = platformRepository.findAll()
    val platforms = doList.map { it.toPlatform() }
    return ListResult.of(platforms)
  }
}
