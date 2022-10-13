package com.zzs.iam.upms.domain.model.org

/**
 * @author 宋志宗 on 2022/8/15
 */
interface PlatformRepository {

  suspend fun save(platformDo: PlatformDO): PlatformDO

  suspend fun delete(platformDo: PlatformDO)

  suspend fun findByCode(code: String): PlatformDO?

  suspend fun findAll(): List<PlatformDO>
}
