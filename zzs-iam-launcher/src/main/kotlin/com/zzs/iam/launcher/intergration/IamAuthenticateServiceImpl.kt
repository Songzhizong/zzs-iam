package com.zzs.iam.launcher.intergration

import com.zzs.iam.gateway.filter.auth.IamAuthenticateService
import com.zzs.iam.upms.application.UserAuthService
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap

/**
 * @author 宋志宗 on 2022/8/17
 */
@Component
class IamAuthenticateServiceImpl(private val userAuthService: UserAuthService) :
  IamAuthenticateService {

  override suspend fun authenticate(
    authorizationHeader: String?,
    tenantId: Long?,
    requestPath: String?,
    apiAuthenticate: Boolean
  ): LinkedMultiValueMap<String, String> {
    return userAuthService.authenticate(authorizationHeader, tenantId, requestPath, apiAuthenticate)
  }
}
