package com.zzs.iam.server.domain.model.user

/**
 * @author 宋志宗 on 2022/8/15
 */
interface UserRepository {

  suspend fun save(userDo: com.zzs.iam.server.domain.model.user.UserDo): com.zzs.iam.server.domain.model.user.UserDo

  suspend fun findById(id: Long): com.zzs.iam.server.domain.model.user.UserDo?

  suspend fun findAllById(ids: Collection<Long>): List<com.zzs.iam.server.domain.model.user.UserDo>

  suspend fun findByPhone(phone: String): com.zzs.iam.server.domain.model.user.UserDo?

  suspend fun findByUniqueIdent(uniqueIdent: String): com.zzs.iam.server.domain.model.user.UserDo?
}
