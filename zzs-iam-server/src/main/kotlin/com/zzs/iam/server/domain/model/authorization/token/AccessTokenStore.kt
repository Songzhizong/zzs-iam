package com.zzs.iam.server.domain.model.authorization.token

import com.zzs.framework.autoconfigure.cache.CacheProperties
import com.zzs.framework.core.date.DateTimes
import com.zzs.framework.core.json.parseJson
import com.zzs.framework.core.json.toJsonString
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * @author 宋志宗 on 2022/8/16
 */
@Component
class AccessTokenStore(
  private val cacheProperties: CacheProperties,
  private val redisTemplate: ReactiveStringRedisTemplate,
) {

  suspend fun saveAccessToken(accessTokenDo: AccessTokenDo) {
    coroutineScope {
      val asyncList = ArrayList<Deferred<*>>()
      val clientId = accessTokenDo.clientId
      val userId = accessTokenDo.authentication.userId
      // 保存access token
      val accessToken = accessTokenDo.value
      val accessTokenKey = genAccessTokenKey(accessToken)
      val validity = accessTokenDo.validity
      val accessTokenJson = accessTokenDo.toJsonString()
      val accessTokenTimeout = Duration.ofSeconds(validity.toLong())
      val valueOps = redisTemplate.opsForValue()
      asyncList.add(async {
        valueOps.setAndAwait(accessTokenKey, accessTokenJson, accessTokenTimeout)
      })
      // user to access
      val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
      asyncList.add(async {
        redisTemplate.opsForList().leftPushAllAndAwait(userIdToAccessKey, accessToken)
        redisTemplate.expireAndAwait(userIdToAccessKey, accessTokenTimeout)
      })

      // 保存refresh token
      val refreshTokenDo = accessTokenDo.refreshToken
      if (refreshTokenDo != null) {
        val refreshToken = refreshTokenDo.value
        val refreshTokenKey = genRefreshTokenKey(refreshToken)
        val refreshTokenValidity = refreshTokenDo.validity
        val refreshTokenJson = refreshTokenDo.toJsonString()
        val refreshTokenTimeout = Duration.ofSeconds(refreshTokenValidity.toLong())
        asyncList.add(async {
          valueOps.setAndAwait(refreshTokenKey, refreshTokenJson, refreshTokenTimeout)
        })
        val userIdToRefreshKey = generateUserIdToRefreshKey(clientId, userId)
        asyncList.add(async {
          redisTemplate.opsForList().leftPushAllAndAwait(userIdToRefreshKey, refreshToken)
          redisTemplate.expireAndAwait(userIdToRefreshKey, refreshTokenTimeout)
        })
      }
      asyncList.forEach { it.await() }
    }
  }

  suspend fun deleteAccessToken(accessToken: String) {
    val accessTokenKey = genAccessTokenKey(accessToken)
    val tokenJson = redisTemplate.opsForValue().getAndAwait(accessTokenKey)
    if (tokenJson.isNullOrBlank()) {
      return
    }
    val accessTokenDo = tokenJson.parseJson(AccessTokenDo::class.java)
    deleteAccessToken(accessTokenDo)
  }

  suspend fun deleteAccessToken(accessTokenDo: AccessTokenDo) {
    val clientId = accessTokenDo.clientId
    val userId = accessTokenDo.authentication.userId
    val accessToken = accessTokenDo.value
    val accessTokenKey = genAccessTokenKey(accessToken)
    redisTemplate.opsForValue().deleteAndAwait(accessTokenKey)
    val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
    redisTemplate.opsForList().removeAndAwait(userIdToAccessKey, 1, accessToken)

    val refreshTokenDo = accessTokenDo.refreshToken
    if (refreshTokenDo != null) {
      val refreshToken = refreshTokenDo.value
      val refreshTokenKey = genRefreshTokenKey(refreshToken)
      redisTemplate.opsForValue().deleteAndAwait(refreshTokenKey)
      val userIdToRefreshKey = generateUserIdToRefreshKey(clientId, userId)
      redisTemplate.opsForList().removeAndAwait(userIdToRefreshKey, 1, refreshToken)
    }
  }

  suspend fun readAccessToken(accessToken: String): AccessTokenDo? {
    val accessTokenKey = genAccessTokenKey(accessToken)
    val valueOps = redisTemplate.opsForValue()
    val tokenJson = valueOps.getAndAwait(accessTokenKey)
    if (tokenJson.isNullOrBlank()) {
      return null
    }
    val accessTokenDo = tokenJson.parseJson(AccessTokenDo::class.java)
    val validity = accessTokenDo.validity
    if (accessTokenDo.isAutoRenewal && accessTokenDo.expiresIn < (validity shr 1)) {
      accessTokenDo.expiration = DateTimes.now().plusSeconds(validity.toLong())
      val jsonString = accessTokenDo.toJsonString()
      val timeout = Duration.ofSeconds(validity.toLong())
      valueOps.set(accessTokenKey, jsonString, timeout).subscribe()
    }
    return accessTokenDo
  }

  suspend fun readRefreshToken(refreshToken: String): RefreshTokenDo? {
    val refreshTokenKey = genRefreshTokenKey(refreshToken)
    val valueOps = redisTemplate.opsForValue()
    val tokenJson = valueOps.getAndAwait(refreshTokenKey)
    if (tokenJson.isNullOrBlank()) {
      return null
    }
    return tokenJson.parseJson(RefreshTokenDo::class.java)
  }

  suspend fun cleanAllToken(clientId: String, userId: String) {
    val deleteKeys = HashSet<String>()
    val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
    val listOps = redisTemplate.opsForList()
    val accessSize = listOps.sizeAndAwait(userIdToAccessKey)
    for (i in 0 until accessSize) {
      listOps.rightPopAndAwait(userIdToAccessKey)?.let { genAccessTokenKey(it) }?.also {
        deleteKeys.add(it)
      }
    }

    val userIdToRefreshKey = generateUserIdToRefreshKey(clientId, userId)
    val refreshSize = listOps.sizeAndAwait(userIdToRefreshKey)
    for (i in 0 until refreshSize) {
      listOps.rightPopAndAwait(userIdToRefreshKey)?.let { genRefreshTokenKey(it) }?.also {
        deleteKeys.add(it)
      }
    }
    if (deleteKeys.size > 0) {
      val array = deleteKeys.toArray(emptyArray<String>())
      redisTemplate.deleteAndAwait(*array)
    }
  }

  suspend fun keepLimit(clientId: String, userId: String, limit: Int) {
    val deletes = ArrayList<String>()
    val listOps = redisTemplate.opsForList()
    val userIdToAccessKey = generateUserIdToAccessKey(clientId, userId)
    val accessSize = listOps.sizeAndAwait(userIdToAccessKey)
    if (accessSize >= limit) {
      val count = accessSize - limit
      for (i in 0..count) {
        listOps.rightPopAndAwait(userIdToAccessKey)?.also {
          deletes.add(genAccessTokenKey(it))
        }
      }

    }
    val userIdToRefreshKey = generateUserIdToRefreshKey(clientId, userId)
    val refreshSize = listOps.sizeAndAwait(userIdToRefreshKey)
    if (refreshSize >= limit) {
      val count = accessSize - limit
      for (i in 0..count) {
        listOps.rightPopAndAwait(userIdToRefreshKey)?.also {
          deletes.add(genRefreshTokenKey(it))
        }
      }
    }
    if (deletes.size > 0) {
      val array = deletes.toArray(emptyArray<String>())
      redisTemplate.deleteAndAwait(*array)
    }
  }


  private fun genAccessTokenKey(value: String): String {
    val prefix = cacheProperties.formattedPrefix()
    return "${prefix}auth_token:access_token:$value"
  }


  private fun genRefreshTokenKey(value: String): String {
    val prefix = cacheProperties.formattedPrefix()
    return "${prefix}auth_token:refresh_token:$value"
  }

  private fun generateUserIdToAccessKey(clientId: String, userId: String): String {
    val prefix = cacheProperties.formattedPrefix()
    return "${prefix}auth_token:user_access:$clientId:$userId"
  }

  private fun generateUserIdToRefreshKey(clientId: String, userId: String): String {
    val prefix = cacheProperties.formattedPrefix()
    return "${prefix}auth_token:user_refresh:$clientId:$userId"
  }
}
