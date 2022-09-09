package com.zzs.iam.server.domain.model.org

/**
 * @author 宋志宗 on 2022/8/15
 */
interface AuthClientRepository {

  suspend fun save(authClientDo: AuthClientDo): AuthClientDo

  suspend fun delete(authClientDo: AuthClientDo)

  suspend fun findByClientId(clientId: String): AuthClientDo?

  suspend fun findAllByPlatform(platform: String): List<AuthClientDo>

  suspend fun findAllByPlatformIn(platforms: Collection<String>): List<AuthClientDo>
}
