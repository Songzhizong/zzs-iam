package com.zzs.iam.server.domain.model.org

import com.zzs.iam.server.dto.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface PlatformUserRepository {

  suspend fun save(platformUserDo: PlatformUserDo): PlatformUserDo

  suspend fun saveAll(platformUsers: Collection<PlatformUserDo>)

  suspend fun delete(platformUserDo: PlatformUserDo)

  suspend fun deleteAll(platformUsers: Collection<PlatformUserDo>)

  suspend fun findById(id: Long): PlatformUserDo?

  suspend fun findByPlatformAndUserId(platform: String, userId: String): PlatformUserDo?

  suspend fun findByPlatformAndUserIdIn(
    platform: String,
    userIds: Collection<String>
  ): List<PlatformUserDo>

  suspend fun findAllByUserId(userId: String): List<PlatformUserDo>

  suspend fun query(platform: String, args: QueryUserArgs): Page<PlatformUserDo>

  suspend fun updateAccountByUserId(userId: String, account: String): Long

  suspend fun updatePhoneByUserId(userId: String, phone: String): Long

  suspend fun updateEmailByUserId(userId: String, email: String): Long
}
