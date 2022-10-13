package com.zzs.iam.server.domain.model.org

import com.zzs.iam.server.dto.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface PlatformUserRepository {

  suspend fun save(platformUserDo: PlatformUserDO): PlatformUserDO

  suspend fun saveAll(platformUsers: Collection<PlatformUserDO>)

  suspend fun delete(platformUserDo: PlatformUserDO)

  suspend fun deleteAll(platformUsers: Collection<PlatformUserDO>)

  suspend fun findById(id: Long): PlatformUserDO?

  suspend fun findByPlatformAndUserId(platform: String, userId: String): PlatformUserDO?

  suspend fun findByPlatformAndUserIdIn(
    platform: String,
    userIds: Collection<String>
  ): List<PlatformUserDO>

  suspend fun findAllByUserId(userId: String): List<PlatformUserDO>

  suspend fun query(platform: String, args: QueryUserArgs): Page<PlatformUserDO>

  suspend fun updateAccountByUserId(userId: String, account: String): Long

  suspend fun updatePhoneByUserId(userId: String, phone: String): Long

  suspend fun updateEmailByUserId(userId: String, email: String): Long
}
