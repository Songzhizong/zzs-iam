package com.zzs.iam.server.domain.model.role

/**
 * @author 宋志宗 on 2022/8/16
 */
interface RoleMenuRelRepository {

  suspend fun insertAll(rels: Collection<RoleMenuRelDo>)

  suspend fun deleteAllByRoleId(roleId: Long): Long

  suspend fun deleteAllByRoleIdAndTerminal(roleId: Long, terminal: String): Long

  suspend fun findAllByRoleIdIn(roleIds: Collection<Long>): List<RoleMenuRelDo>
}
