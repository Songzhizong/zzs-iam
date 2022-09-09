package com.zzs.iam.server.domain.model.org

import com.zzs.iam.server.dto.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface PlatformUserRepository {

  suspend fun save(platformUserDo: com.zzs.iam.server.domain.model.org.PlatformUserDo): com.zzs.iam.server.domain.model.org.PlatformUserDo

  suspend fun saveAll(platformUsers: Collection<com.zzs.iam.server.domain.model.org.PlatformUserDo>)

  suspend fun delete(platformUserDo: com.zzs.iam.server.domain.model.org.PlatformUserDo)

  suspend fun deleteAll(platformUsers: Collection<com.zzs.iam.server.domain.model.org.PlatformUserDo>)

  suspend fun findById(id: Long): com.zzs.iam.server.domain.model.org.PlatformUserDo?

  suspend fun findByPlatformAndUserId(platform: String, userId: String): com.zzs.iam.server.domain.model.org.PlatformUserDo?

  suspend fun findByPlatformAndUserIdIn(
    platform: String,
    userIds: Collection<String>
  ): List<com.zzs.iam.server.domain.model.org.PlatformUserDo>

  suspend fun findAllByUserId(userId: String): List<com.zzs.iam.server.domain.model.org.PlatformUserDo>

  suspend fun query(platform: String, args: com.zzs.iam.server.dto.args.QueryUserArgs): Page<com.zzs.iam.server.domain.model.org.PlatformUserDo>

  suspend fun updateAccountByUserId(userId: String, account: String): Long

  suspend fun updatePhoneByUserId(userId: String, phone: String): Long

  suspend fun updateEmailByUserId(userId: String, email: String): Long
}
