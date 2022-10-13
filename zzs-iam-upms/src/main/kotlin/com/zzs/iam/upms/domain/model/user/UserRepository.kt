package com.zzs.iam.upms.domain.model.user

/**
 * @author 宋志宗 on 2022/8/15
 */
interface UserRepository {

  suspend fun save(userDo: UserDO): UserDO

  suspend fun findById(id: Long): UserDO?

  suspend fun findAllById(ids: Collection<Long>): List<UserDO>

  suspend fun findByPhone(phone: String): UserDO?

  suspend fun findByUniqueIdent(uniqueIdent: String): UserDO?
}
