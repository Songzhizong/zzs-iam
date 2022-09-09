package com.zzs.iam.gateway.filter.auth

import org.springframework.util.LinkedMultiValueMap

/**
 * @author 宋志宗 on 2022/8/17
 */
interface IamAuthenticateService {

  suspend fun authenticate(
    authorizationHeader: String?,
    tenantId: Long?,
    requestPath: String?,
    apiAuthenticate: Boolean
  ): LinkedMultiValueMap<String, String>
}
