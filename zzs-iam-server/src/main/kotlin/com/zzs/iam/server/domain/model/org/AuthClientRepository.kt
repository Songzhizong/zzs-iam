package com.zzs.iam.server.domain.model.org

/**
 * @author 宋志宗 on 2022/8/15
 */
interface AuthClientRepository {

  suspend fun save(authClientDo: AuthClientDO): AuthClientDO

  suspend fun delete(authClientDo: AuthClientDO)

  suspend fun findByClientId(clientId: String): AuthClientDO?

  suspend fun findAllByPlatform(platform: String): List<AuthClientDO>

  suspend fun findAllByPlatformIn(platforms: Collection<String>): List<AuthClientDO>
}
