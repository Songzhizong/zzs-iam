package com.zzs.iam.upms.domain.model.user

/**
 * @author 宋志宗 on 2022/8/24
 */
interface OtherPlatAuthRepository {

  suspend fun save(otherPlatAuthDo: OtherPlatAuthDO): OtherPlatAuthDO

  suspend fun delete(otherPlatAuthDo: OtherPlatAuthDO)

  suspend fun deleteAllByUserId(userId: Long)

  suspend fun deleteByUserIdAndPlatCode(userId: Long, platCode: String)

  suspend fun findByPlatCodeAndOtherPlatUserId(
    platCode: String,
    otherPlatUserId: String
  ): OtherPlatAuthDO?

  suspend fun findByUserIdAndPlatCode(userId: Long, platCode: String): OtherPlatAuthDO?
}
