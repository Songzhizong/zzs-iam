package com.zzs.iam.server.domain.model.org

import com.zzs.iam.server.dto.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TenantUserRepository {

  suspend fun save(tenantUserDo: TenantUserDo): TenantUserDo

  suspend fun saveAll(tenantUsers: Collection<TenantUserDo>)

  suspend fun delete(tenantUserDo: TenantUserDo)

  suspend fun deleteAll(tenantUsers: Collection<TenantUserDo>)

  suspend fun findAllByUserId(userId: String): List<TenantUserDo>

  suspend fun findByTenantIdAndUserId(tenantId: Long, userId: String): TenantUserDo?

  suspend fun findByTenantIdAndUserIdIn(
    tenantId: Long,
    userIds: Collection<String>
  ): List<TenantUserDo>

  suspend fun findAllByPlatformAndUserId(platform: String, userId: String): List<TenantUserDo>

  suspend fun query(tenantId: Long, args: QueryUserArgs): Page<TenantUserDo>

  suspend fun existsByPlatformAndUserId(platform: String, userId: String): Boolean

  suspend fun existsByPlatformAndUserIdIn(platform: String, userIds: Collection<String>): Boolean

  suspend fun existsByTenantId(tenantId: Long): Boolean

  suspend fun updateAccountByUserId(userId: String, account: String): Long

  suspend fun updatePhoneByUserId(userId: String, phone: String): Long

  suspend fun updateEmailByUserId(userId: String, email: String): Long
}
