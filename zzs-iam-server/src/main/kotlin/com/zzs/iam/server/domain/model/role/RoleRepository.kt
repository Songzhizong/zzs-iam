package com.zzs.iam.server.domain.model.role

/**
 * @author 宋志宗 on 2022/8/15
 */
interface RoleRepository {

  suspend fun save(roleDo: RoleDo): RoleDo

  suspend fun delete(roleDo: RoleDo)

  suspend fun findById(id: Long): RoleDo?

  suspend fun findAllById(ids: Collection<Long>): List<RoleDo>

  suspend fun findAll(platform: String, tenantId: Long?): List<RoleDo>

  suspend fun findAllBasic(platform: String, tenantId: Long?): List<RoleDo>

  suspend fun findTenantAdmin(platform: String, tenantId: Long): RoleDo?
}
