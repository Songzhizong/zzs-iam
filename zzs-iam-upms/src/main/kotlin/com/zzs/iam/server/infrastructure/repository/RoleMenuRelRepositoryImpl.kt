package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.role.RoleMenuRelDO
import com.zzs.iam.server.domain.model.role.RoleMenuRelRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/17
 */
@Repository
class RoleMenuRelRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : RoleMenuRelRepository {
  private val clazz = RoleMenuRelDO::class.java

  override suspend fun insertAll(rels: Collection<RoleMenuRelDO>) {
    rels.forEach { it.id = idGenerator.generate() }
    mongoTemplate.insertAll(rels).awaitLast()
  }

  override suspend fun deleteAllByRoleId(roleId: Long): Long {
    val criteria = Criteria.where("roleId").`is`(roleId)
    val query = Query.query(criteria)
    return mongoTemplate.remove(query, clazz).awaitSingle().deletedCount
  }

  override suspend fun deleteAllByRoleIdAndTerminal(roleId: Long, terminal: String): Long {
    val criteria = Criteria.where("roleId").`is`(roleId).and("terminal").`is`(terminal)
    val query = Query.query(criteria)
    return mongoTemplate.remove(query, clazz).awaitSingle().deletedCount
  }

  override suspend fun findAllByRoleIdIn(roleIds: Collection<Long>): List<RoleMenuRelDO> {
    val criteria = Criteria.where("roleId").`in`(roleIds)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }
}
