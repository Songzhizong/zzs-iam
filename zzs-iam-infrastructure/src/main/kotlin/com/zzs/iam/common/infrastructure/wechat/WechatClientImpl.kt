package com.zzs.iam.common.infrastructure.wechat

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.InternalServerException
import com.zzs.framework.core.json.parseJson
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.common.infrastructure.wechat.resp.AccessTokenResp
import com.zzs.iam.common.infrastructure.wechat.resp.Code2SessionResp
import com.zzs.iam.common.infrastructure.wechat.resp.PhoneNumberResp
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 宋志宗 on 2022/8/24
 */
class WechatClientImpl(
  private val webClient: WebClient,
  private val properties: WechatProperties,
) : WechatClient {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(WechatClientImpl::class.java)
  }

  override suspend fun getOpenId(code: String): String {
    val resp = code2Session(code)
    if (resp.isFailure) {
      val msg = "获取openId失败: ${resp.errCode} ${resp.errMsg}"
      log.info(msg)
      throw InternalServerException(msg)
    }
    return resp.openId
  }

  override suspend fun code2Session(code: String): Code2SessionResp {
    val baseUrl = properties.codeToSessionUrl.requireNotBlank { "未配置微信访问地址" }
    val appId = properties.appId.requireNotBlank { "微信appid为空" }
    val appSecret = properties.appSecret.requireNotBlank { "微信appSecret配置为空" }
    val url = "$baseUrl?appid=$appId&secret=$appSecret&js_code=$code&grant_type=authorization_code"
    val body = webClient.get().uri(url)
      .exchangeToMono { response ->
        response.bodyToMono(String::class.java)
          .defaultIfEmpty("")
          .doOnNext { s ->
            val httpStatus = response.statusCode()
            if (httpStatus.isError) {
              log.info("code2Session返回错误的状态码: ${httpStatus.value()} $s")
              throw InternalServerException("code2Session返回错误的状态码: ${httpStatus.value()}")
            }
          }
      }.awaitSingle()
    return body.parseJson(Code2SessionResp::class.java)
  }

  override suspend fun getUserPhoneNumber(code: String): String {
    val baseUrl = properties.phoneNumberUrl.requireNotBlank { "获取微信手机号方位地址未配置" }
    val accessToken = getAccessToken()
    val url = "$baseUrl?access_token=$accessToken"
    val body = webClient.post().uri(url)
      .contentType(MediaType.APPLICATION_JSON)
      .header("Accept", "application/json")
      .body(BodyInserters.fromValue(mapOf("code" to code)))
      .exchangeToMono { response ->
        val httpStatus = response.statusCode()
        response.bodyToMono(String::class.java)
          .defaultIfEmpty("")
          .doOnNext { s ->
            if (httpStatus.isError) {
              log.info("获取微信手机号返回错误的状态码: ${httpStatus.value()} $s")
              throw InternalServerException("获取微信手机号返回错误的状态码: ${httpStatus.value()}")
            }
          }
      }.awaitSingle()
    val resp = body.parseJson(PhoneNumberResp::class.java)
    val phoneInfo = resp.phoneInfo
    val errCode = resp.errCode
    if (errCode == "-1") {
      throw BadRequestException("系统繁忙, 请稍后再试")
    }
    if (errCode == "40029") {
      throw BadRequestException("无效的code")
    }
    if (phoneInfo == null) {
      log.info("获取微信手机号失败: {}", body)
      val msg = "获取微信手机号失败: $errCode ${resp.errMsg}"
      throw InternalServerException(msg)
    }
    val purePhoneNumber = phoneInfo.purePhoneNumber
    if (purePhoneNumber.isNullOrBlank()) {
      log.info("获取微信手机号失败: {}", body)
      val msg = "获取微信手机号失败: $errCode ${resp.errMsg}"
      throw InternalServerException(msg)
    }
    return purePhoneNumber
  }


  private suspend fun getAccessToken(): String {
    val baseUrl = properties.accessTokenUrl.requireNotBlank { "微信获取accessToken地址配置为空" }
    val appId = properties.appId.requireNotBlank { "微信appid为空" }
    val appSecret = properties.appSecret.requireNotBlank { "微信appSecret配置为空" }
    val url = "$baseUrl?grant_type=client_credential&appid=$appId&secret=$appSecret"
    val body = webClient.get().uri(url)
      .exchangeToMono { response ->
        response.bodyToMono(String::class.java)
          .defaultIfEmpty("")
          .doOnNext { s ->
            val httpStatus = response.statusCode()
            if (httpStatus.isError) {
              val msg = "获取微信accessToken失败: ${httpStatus.value()} $s"
              log.info(msg)
              throw InternalServerException(msg)
            }
          }
      }.awaitSingle()
    val resp = body.parseJson(AccessTokenResp::class.java)
    val accessToken = resp.accessToken
    if (accessToken.isNullOrBlank()) {
      log.info("获取微信accessToken失败: {}", body)
      val msg = "获取微信accessToken失败: ${resp.errCode} ${resp.errMsg}"
      throw InternalServerException(msg)
    }
    log.debug("获取微信access token返回结果; {}", body)
    return accessToken
  }
}
