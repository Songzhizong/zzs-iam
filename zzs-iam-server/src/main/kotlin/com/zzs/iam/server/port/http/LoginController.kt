package com.zzs.iam.server.port.http

import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.server.application.AuthClientService
import com.zzs.iam.server.application.LoginService
import com.zzs.iam.server.domain.model.authorization.token.AccessToken
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import java.util.*

/**
 * 登录相关
 *
 * @author 宋志宗 on 2022/8/15
 */
@RestController
@RequestMapping("/iam/login")
class LoginController(
  private val loginService: LoginService,
  private val authClientService: AuthClientService
) {

  /**
   * 密码登录
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/login/password
   *   Content-Type: application/x-www-form-urlencoded
   *   Authorization: Basic aWFtX3dlYjppYW1fd2Vi
   *
   *   username=general&password=Z2VRSEpGOWZrZTJnR1RGaWhuaHVSb1JCZnBoUFdZTTc=&rememberMe=true
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "token_type": "Bearer",
   *       "access_token": "ac3e3cacc407445d9a57967ebdc100fc",
   *       "refresh_token": "dacfad1797f947ff9c22c1b138b16ac4",
   *       "expires_in": 3600
   *     }
   *   }
   * </pre>
   *
   * @param username      用户唯一标识(账号/邮箱/手机号)
   * @param password      密码
   * @param ptype         密码编码方式, 默认base64, plain: 明文
   * @param rememberMe    用于控制是否生成refresh token
   * @param authorization authClient basic token
   */
  @PostMapping("/password")
  suspend fun passwordLogin(
    username: String?,
    password: String?,
    rememberMe: Boolean?,
    ptype: String?,
    @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false)
    authorization: String?,
    exchange: ServerWebExchange
  ): Result<AccessToken> {
    authorization.requireNotBlank { "Basic authorization header is blank" }
    val authClient = authClientService.authenticate(authorization)
    val formData = exchange.formData.awaitSingleOrNull()
    val username1 = formData?.getFirst("username")
      ?.ifBlank { null }.requireNotBlank { "唯一身份标识为空" }
    val ptype1 = formData?.getFirst("ptype")
    val password1 =
      (formData?.getFirst("password")?.ifBlank { null }.requireNotBlank { "密码为空" })
        .let {
          if ("plain".equals(ptype1, true)) {
            return@let it
          }
          Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8)
        }
    val rememberMe1 = formData?.getFirst("rememberMe")?.ifBlank { null }?.toBoolean() ?: false
    val accessToken = loginService.passwordLogin(
      authClient, username1, password1, rememberMe1
    )
    return Result.success(accessToken)
  }
}
