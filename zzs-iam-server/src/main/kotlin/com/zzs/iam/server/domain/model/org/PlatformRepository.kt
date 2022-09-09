package com.zzs.iam.server.domain.model.org

/**
 * @author 宋志宗 on 2022/8/15
 */
interface PlatformRepository {

  suspend fun save(platformDo: com.zzs.iam.server.domain.model.org.PlatformDo): com.zzs.iam.server.domain.model.org.PlatformDo

  suspend fun delete(platformDo: com.zzs.iam.server.domain.model.org.PlatformDo)

  suspend fun findByCode(code: String): com.zzs.iam.server.domain.model.org.PlatformDo?

  suspend fun findAll(): List<com.zzs.iam.server.domain.model.org.PlatformDo>
}
