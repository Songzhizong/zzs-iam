package com.zzs.iam.server.domain.model.role

/**
 * @author 宋志宗 on 2022/8/15
 */
interface RoleRepository {

  suspend fun save(roleDo: RoleDO): RoleDO

  suspend fun delete(roleDo: RoleDO)

  suspend fun findById(id: Long): RoleDO?

  suspend fun findAllById(ids: Collection<Long>): List<RoleDO>

  suspend fun findAll(platform: String, tenantId: Long?): List<RoleDO>

  suspend fun findAllBasic(platform: String, tenantId: Long?): List<RoleDO>

  suspend fun findTenantAdmin(platform: String, tenantId: Long): RoleDO?
}
