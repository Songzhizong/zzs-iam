package com.zzs.iam.server.domain.model.user

/**
 * @author 宋志宗 on 2022/8/24
 */
interface OtherPlatAuthRepository {

  suspend fun save(otherPlatAuthDo: OtherPlatAuthDo): OtherPlatAuthDo

  suspend fun delete(otherPlatAuthDo: OtherPlatAuthDo)

  suspend fun deleteAllByUserId(userId: Long)

  suspend fun deleteByUserIdAndPlatCode(userId: Long, platCode: String)

  suspend fun findByPlatCodeAndOtherPlatUserId(
    platCode: String,
    otherPlatUserId: String
  ): OtherPlatAuthDo?

  suspend fun findByUserIdAndPlatCode(userId: Long, platCode: String): OtherPlatAuthDo?
}
