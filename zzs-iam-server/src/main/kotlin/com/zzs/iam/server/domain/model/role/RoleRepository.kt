package com.zzs.iam.server.domain.model.role

/**
 * @author 宋志宗 on 2022/8/15
 */
interface RoleRepository {

  suspend fun save(roleDo: com.zzs.iam.server.domain.model.role.RoleDo): com.zzs.iam.server.domain.model.role.RoleDo

  suspend fun delete(roleDo: com.zzs.iam.server.domain.model.role.RoleDo)

  suspend fun findById(id: Long): com.zzs.iam.server.domain.model.role.RoleDo?

  suspend fun findAllById(ids: Collection<Long>): List<com.zzs.iam.server.domain.model.role.RoleDo>

  suspend fun findAll(platform: String, tenantId: Long?): List<com.zzs.iam.server.domain.model.role.RoleDo>

  suspend fun findAllBasic(platform: String, tenantId: Long?): List<com.zzs.iam.server.domain.model.role.RoleDo>

  suspend fun findTenantAdmin(platform: String, tenantId: Long): com.zzs.iam.server.domain.model.role.RoleDo?
}
