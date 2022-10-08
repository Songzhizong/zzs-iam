package com.zzs.iam.gateway.filter.auth

import cn.idealframework2.exception.ForbiddenException
import cn.idealframework2.exception.UnauthorizedException
import org.springframework.util.LinkedMultiValueMap

/**
 * @author 宋志宗 on 2022/8/17
 */
interface IamAuthenticateService {

  /**
   * 执行身份认证
   *
   * @param authorizationHeader    Authorization头
   * @param tenantId               租户id
   * @param requestPath            请求的URI地址
   * @param apiAuthenticate        是否进行API级别的身份认证
   * @throws UnauthorizedException 未登录时抛出此异常
   * @throws ForbiddenException    没有访问权限时抛出此异常
   * @return 认证通过后附加的请求头, 这些http头会附加到发送到后端服务的请求中
   */
  @Throws(UnauthorizedException::class, ForbiddenException::class)
  suspend fun authenticate(
    authorizationHeader: String?,
    tenantId: Long?,
    requestPath: String?,
    apiAuthenticate: Boolean
  ): LinkedMultiValueMap<String, String>
}
