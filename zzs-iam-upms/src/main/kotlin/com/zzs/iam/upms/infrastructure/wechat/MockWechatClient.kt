package com.zzs.iam.upms.infrastructure.wechat

import com.zzs.iam.upms.infrastructure.wechat.resp.Code2SessionResp
import java.util.*

/**
 * @author 宋志宗 on 2022/8/24
 */
class MockWechatClient(
  private val properties: WechatProperties
) : WechatClient {

  override suspend fun getOpenId(code: String): String {
    return properties.mock.openId
  }

  override suspend fun code2Session(code: String): Code2SessionResp {
    return Code2SessionResp()
      .setOpenId(properties.mock.openId)
      .setSessionKey(UUID.randomUUID().toString())
      .setUnionId(properties.mock.openId)
      .setErrCode(0)
      .setErrMsg("success")
  }

  override suspend fun getUserPhoneNumber(code: String): String {
    return properties.mock.phoneNumber
  }
}
