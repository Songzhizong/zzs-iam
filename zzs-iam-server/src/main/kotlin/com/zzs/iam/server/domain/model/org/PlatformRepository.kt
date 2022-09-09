package com.zzs.iam.server.domain.model.org

/**
 * @author 宋志宗 on 2022/8/15
 */
interface PlatformRepository {

  suspend fun save(platformDo: PlatformDo): PlatformDo

  suspend fun delete(platformDo: PlatformDo)

  suspend fun findByCode(code: String): PlatformDo?

  suspend fun findAll(): List<PlatformDo>
}
