package com.zzs.iam.server.port.http

import com.zzs.framework.core.trace.Operation
import com.zzs.framework.core.transmission.ListResult
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.pojo.Terminal
import com.zzs.iam.server.application.TerminalService
import com.zzs.iam.server.domain.model.front.TerminalRepository
import com.zzs.iam.server.dto.args.CreateTerminalArgs
import com.zzs.iam.server.dto.args.UpdateTerminalArgs
import org.springframework.web.bind.annotation.*

/**
 * 终端管理
 *
 * @author 宋志宗 on 2022/8/15
 */
@RestController
@RequestMapping("/iam/terminal")
class TerminalController(
  private val terminalService: TerminalService,
  private val terminalRepository: TerminalRepository
) {

  /**
   * 新增终端
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST&nbsp;{{base_url}}/iam/terminal/create_terminal
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "platform": "iam",
   *     "code": "iam_web",
   *     "name": "IAM WEB端",
   *     "note": ""
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "code": "iam_web",
   *       "platform": "iam",
   *       "name": "IAM WEB端",
   *       "note": "",
   *       "createdTime": "2022-08-20 01:03:49.186",
   *       "updatedTime": "2022-08-20 01:03:49.186"
   *     }
   *   }
   * </pre>
   */
  @Operation("新增终端")
  @PostMapping("/create_terminal")
  suspend fun create(@RequestBody args: CreateTerminalArgs): Result<Terminal> {
    val terminalDo = terminalService.create(args)
    val terminal = terminalDo.toTerminal()
    return Result.success(terminal)
  }

  /**
   * 删除终端
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST&nbsp;{{base_url}}/iam/terminal/delete_terminal?code=iam_wechat
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   *
   * @param code 终端编码
   */
  @Operation("删除终端")
  @PostMapping("/delete_terminal")
  suspend fun delete(code: String): Result<Void> {
    terminalService.delete(code)
    return Result.success()
  }

  /**
   * 修改终端信息
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST&nbsp;{{base_url}}/iam/terminal/update_terminal?code=iam_web
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "name": "IAM WEB端",
   *     "note": "WEB端"
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "code": "iam_web",
   *       "platform": "iam",
   *       "name": "IAM WEB端",
   *       "note": "WEB端",
   *       "createdTime": "2022-08-20 01:03:49.186",
   *       "updatedTime": "2022-08-20 01:03:49.186"
   *     }
   *   }
   * </pre>
   */
  @Operation("修改终端信息")
  @PostMapping("/update_terminal")
  suspend fun update(code: String?, @RequestBody args: UpdateTerminalArgs): Result<Terminal> {
    code.requireNotBlank { "终端编码为空" }
    val name = args.name.requireNotBlank { "终端名称为空" }
    val note = args.note
    val terminalDo = terminalService.update(code, name, note)
    val terminal = terminalDo.toTerminal()
    return Result.success(terminal)
  }

  /**
   * 获取指定平台下所有的终端
   * <pre>
   *   <p><b>请求示例</b></p>
   *   GET&nbsp;{{base_url}}/iam/terminal/find_all_by_platform?platform=iam
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": [
   *       {
   *         "code": "iam_web",
   *         "platform": "iam",
   *         "name": "IAM WEB端",
   *         "note": "",
   *         "createdTime": "2022-08-20 01:03:49.186",
   *         "updatedTime": "2022-08-20 01:03:49.186"
   *       },
   *       {
   *         "code": "iam_wechat",
   *         "platform": "iam",
   *         "name": "IAM 微信端",
   *         "note": "微信端",
   *         "createdTime": "2022-08-20 01:04:32.114",
   *         "updatedTime": "2022-08-20 01:04:32.114"
   *       }
   *     ]
   *   }
   * </pre>
   *
   * @param platform 平台编码
   */
  @GetMapping("/find_all_by_platform")
  suspend fun findAllBYPlatform(platform: String): ListResult<Terminal> {
    val list = terminalRepository.findAllByPlatform(platform)
    val terminalList = list.map { it.toTerminal() }
    return ListResult.of(terminalList)
  }
}
