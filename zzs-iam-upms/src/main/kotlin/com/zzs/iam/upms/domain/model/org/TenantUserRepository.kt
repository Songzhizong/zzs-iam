package com.zzs.iam.upms.domain.model.org

import com.zzs.iam.upms.dto.args.QueryUserArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TenantUserRepository {

  suspend fun save(tenantUserDo: TenantUserDO): TenantUserDO

  suspend fun saveAll(tenantUsers: Collection<TenantUserDO>)

  suspend fun delete(tenantUserDo: TenantUserDO)

  suspend fun deleteAll(tenantUsers: Collection<TenantUserDO>)

  suspend fun findAllByUserId(userId: String): List<TenantUserDO>

  suspend fun findByTenantIdAndUserId(tenantId: Long, userId: String): TenantUserDO?

  suspend fun findByTenantIdAndUserIdIn(
    tenantId: Long,
    userIds: Collection<String>
  ): List<TenantUserDO>

  suspend fun findAllByPlatformAndUserId(platform: String, userId: String): List<TenantUserDO>

  suspend fun query(tenantId: Long, args: QueryUserArgs): Page<TenantUserDO>

  suspend fun existsByPlatformAndUserId(platform: String, userId: String): Boolean

  suspend fun existsByPlatformAndUserIdIn(platform: String, userIds: Collection<String>): Boolean

  suspend fun existsByTenantId(tenantId: Long): Boolean

  suspend fun updateAccountByUserId(userId: String, account: String): Long

  suspend fun updatePhoneByUserId(userId: String, phone: String): Long

  suspend fun updateEmailByUserId(userId: String, email: String): Long
}
