package com.zzs.iam.server.domain.model.user

/**
 * @author 宋志宗 on 2022/8/24
 */
interface OtherPlatAuthRepository {

  suspend fun save(otherPlatAuthDo: com.zzs.iam.server.domain.model.user.OtherPlatAuthDo): com.zzs.iam.server.domain.model.user.OtherPlatAuthDo

  suspend fun delete(otherPlatAuthDo: com.zzs.iam.server.domain.model.user.OtherPlatAuthDo)

  suspend fun deleteAllByUserId(userId: Long)

  suspend fun deleteByUserIdAndPlatCode(userId: Long, platCode: String)

  suspend fun findByPlatCodeAndOtherPlatUserId(
    platCode: String,
    otherPlatUserId: String
  ): com.zzs.iam.server.domain.model.user.OtherPlatAuthDo?

  suspend fun findByUserIdAndPlatCode(userId: Long, platCode: String): com.zzs.iam.server.domain.model.user.OtherPlatAuthDo?
}
