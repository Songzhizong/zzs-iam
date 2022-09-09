package com.zzs.iam.server.domain.model.org

/**
 * @author 宋志宗 on 2022/8/15
 */
interface AuthClientRepository {

  suspend fun save(authClientDo: com.zzs.iam.server.domain.model.org.AuthClientDo): com.zzs.iam.server.domain.model.org.AuthClientDo

  suspend fun delete(authClientDo: com.zzs.iam.server.domain.model.org.AuthClientDo)

  suspend fun findByClientId(clientId: String): com.zzs.iam.server.domain.model.org.AuthClientDo?

  suspend fun findAllByPlatform(platform: String): List<com.zzs.iam.server.domain.model.org.AuthClientDo>

  suspend fun findAllByPlatformIn(platforms: Collection<String>): List<com.zzs.iam.server.domain.model.org.AuthClientDo>
}
