package com.zzs.iam.server.domain.model.user

/**
 * @author 宋志宗 on 2022/8/15
 */
interface UserRepository {

  suspend fun save(userDo: UserDo): UserDo

  suspend fun findById(id: Long): UserDo?

  suspend fun findAllById(ids: Collection<Long>): List<UserDo>

  suspend fun findByPhone(phone: String): UserDo?

  suspend fun findByUniqueIdent(uniqueIdent: String): UserDo?
}
