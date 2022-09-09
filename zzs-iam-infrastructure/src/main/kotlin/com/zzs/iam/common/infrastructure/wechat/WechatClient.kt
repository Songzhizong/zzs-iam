package com.zzs.iam.common.infrastructure.wechat

import com.zzs.iam.common.infrastructure.wechat.resp.Code2SessionResp

/**
 * @author 宋志宗 on 2022/8/24
 */
interface WechatClient {

  suspend fun getOpenId(code: String): String

  suspend fun code2Session(code: String): Code2SessionResp

  suspend fun getUserPhoneNumber(code: String): String
}
