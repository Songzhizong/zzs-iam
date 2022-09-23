package com.zzs.iam.server.infrastructure.repository

import com.zzs.framework.core.spring.toPageable
import com.zzs.iam.server.domain.model.org.TenantDO
import com.zzs.iam.server.domain.model.org.TenantRepository
import com.zzs.iam.server.dto.args.QueryTenantArgs
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/15
 */
@Repository
class TenantRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : TenantRepository {
  private val clazz = TenantDO::class.java

  override suspend fun save(tenantDo: TenantDO): TenantDO {
    if (tenantDo.id < 1) {
      tenantDo.id = idGenerator.generate()
      return mongoTemplate.insert(tenantDo).awaitSingle()
    }
    return mongoTemplate.save(tenantDo).awaitSingle()
  }

  override suspend fun delete(tenantDo: TenantDO) {
    mongoTemplate.remove(tenantDo).awaitSingle()
  }

  override suspend fun findById(id: Long): TenantDO? {
    val criteria = Criteria.where("id").`is`(id)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Collection<Long>): List<TenantDO> {
    val criteria = Criteria.where("id").`in`(ids)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAllChild(parentRouter: String): List<TenantDO> {
    val criteria = Criteria.where("parentRouter").regex("^$parentRouter.*$")
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun existsByParentId(parentId: Long): Boolean {
    val criteria = Criteria.where("parentId").`is`(parentId)
    val query = Query.query(criteria)
    return mongoTemplate.exists(query, clazz).awaitSingle()
  }

  override suspend fun query(platform: String, args: QueryTenantArgs): Page<TenantDO> {
    val paging = args.paging.descBy("id")
    val name = args.name?.ifBlank { null }

    var customCondition = false
    val criteria = Criteria.where("platform").`is`(platform)
    name?.let {
      customCondition = true
      criteria.and("name").regex("^.*$it.*$")
    }
    if (!customCondition) {
      criteria.and("parentId").`is`(-1L)
    }
    val pageable = paging.toPageable()
    val query = Query.query(criteria).with(pageable)
    val content = mongoTemplate.find(query, clazz).collectList().awaitSingle()
    if (content.size < pageable.pageSize) {
      return PageImpl(content, pageable, 0)
    }
    val count = Query.query(criteria)
    val total = mongoTemplate.count(count, clazz).awaitSingle()
    return PageImpl(content, pageable, total)
  }
}
