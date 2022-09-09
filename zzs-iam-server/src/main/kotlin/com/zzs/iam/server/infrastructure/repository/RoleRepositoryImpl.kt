package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.common.constants.RoleType
import com.zzs.iam.server.domain.model.role.RoleDo
import com.zzs.iam.server.domain.model.role.RoleRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/15
 */
@Repository
class RoleRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : RoleRepository {
  private val clazz = RoleDo::class.java

  override suspend fun save(roleDo: RoleDo): RoleDo {
    if (roleDo.id < 1) {
      roleDo.id = idGenerator.generate()
      return mongoTemplate.insert(roleDo).awaitSingle()
    }
    return mongoTemplate.save(roleDo).awaitSingle()
  }

  override suspend fun delete(roleDo: RoleDo) {
    mongoTemplate.remove(roleDo).awaitSingle()
  }

  override suspend fun findById(id: Long): RoleDo? {
    val criteria = Criteria.where("id").`is`(id)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Collection<Long>): List<RoleDo> {
    val criteria = Criteria.where("id").`in`(ids)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAll(platform: String, tenantId: Long?): List<RoleDo> {
    val criteria = Criteria.where("platform").`is`(platform)
    tenantId?.also { criteria.and("tenantId").`is`(tenantId) }
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAllBasic(platform: String, tenantId: Long?): List<RoleDo> {
    val criteria = Criteria.where("platform").`is`(platform).and("basic").`is`(true)
    tenantId?.also { criteria.and("tenantId").`is`(tenantId) }
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findTenantAdmin(platform: String, tenantId: Long): RoleDo? {
    val criteria = Criteria.where("platform").`is`(platform)
      .and("tenantId").`is`(tenantId)
      .and("type").`is`(RoleType.ADMIN)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }
}
