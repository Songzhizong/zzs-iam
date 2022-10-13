package com.zzs.iam.server.infrastructure.repository

import cn.idealframework2.spring.toPageable
import com.zzs.iam.server.domain.model.org.TenantUserDO
import com.zzs.iam.server.domain.model.org.TenantUserRepository
import com.zzs.iam.server.dto.args.QueryUserArgs
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * @author 宋志宗 on 2022/8/15
 */
@Repository
class TenantUserRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : TenantUserRepository {
  private val clazz = TenantUserDO::class.java

  override suspend fun save(tenantUserDo: TenantUserDO): TenantUserDO {
    if (tenantUserDo.id < 1) {
      tenantUserDo.id = idGenerator.generate()
      return mongoTemplate.insert(tenantUserDo).awaitSingle()
    }
    return mongoTemplate.save(tenantUserDo).awaitSingle()
  }

  override suspend fun saveAll(tenantUsers: Collection<TenantUserDO>) {
    tenantUsers.forEach {
      if (it.id < 1) {
        it.id = idGenerator.generate()
      }
    }
    Flux.fromIterable(tenantUsers).flatMap { mongoTemplate.save(it) }.awaitLast()
  }

  override suspend fun delete(tenantUserDo: TenantUserDO) {
    mongoTemplate.remove(tenantUserDo).awaitSingle()
  }

  override suspend fun deleteAll(tenantUsers: Collection<TenantUserDO>) {
    val ids = tenantUsers.mapTo(HashSet()) { it.id }.toTypedArray()
    val criteria = Criteria.where("id").`in`(*ids)
    val query = Query.query(criteria)
    mongoTemplate.remove(query, clazz).awaitSingle()
  }

  override suspend fun findAllByUserId(userId: String): List<TenantUserDO> {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findByTenantIdAndUserId(tenantId: Long, userId: String): TenantUserDO? {
    val criteria = Criteria.where("tenantId").`is`(tenantId).and("userId").`is`(userId)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findByTenantIdAndUserIdIn(
    tenantId: Long,
    userIds: Collection<String>
  ): List<TenantUserDO> {
    val criteria = Criteria.where("tenantId").`is`(tenantId).and("userId").`in`(userIds)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAllByPlatformAndUserId(
    platform: String,
    userId: String
  ): List<TenantUserDO> {
    val criteria = Criteria.where("platform").`is`(platform).and("userId").`is`(userId)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  @Suppress("DuplicatedCode")
  override suspend fun query(tenantId: Long, args: QueryUserArgs): Page<TenantUserDO> {
    val paging = args.paging.descBy("id")
    val uniqueIdent = args.uniqueIdent?.ifBlank { null }
    val criteria = Criteria.where("tenantId").`is`(tenantId)
    uniqueIdent?.also {
      val encrypt = TenantUserDO.encrypt(it)
      val c1 = Criteria.where("account").`is`(it)
      val c2 = Criteria.where("phone").`is`(encrypt)
      val c3 = Criteria.where("email").`is`(encrypt)
      criteria.orOperator(c1, c2, c3)
    }
    val pageable = paging.toPageable()
    val query = Query(criteria).with(pageable)
    val content = mongoTemplate.find(query, clazz)
      .collectList().awaitSingle()
    if (content.size < paging.pageSize) {
      return PageImpl(content, pageable, 0)
    }
    val countQuery = Query(criteria)
    val total = mongoTemplate.count(countQuery, clazz).awaitSingle()
    return PageImpl(content, pageable, total)
  }

  override suspend fun existsByPlatformAndUserId(platform: String, userId: String): Boolean {
    val criteria = Criteria.where("platform").`is`(platform).and("userId").`is`(userId)
    val query = Query.query(criteria)
    return mongoTemplate.exists(query, clazz).awaitSingle()
  }

  override suspend fun existsByPlatformAndUserIdIn(
    platform: String,
    userIds: Collection<String>
  ): Boolean {
    val criteria = Criteria.where("platform").`is`(platform).and("userId").`in`(userIds)
    val query = Query.query(criteria)
    return mongoTemplate.exists(query, clazz).awaitSingle()
  }

  override suspend fun existsByTenantId(tenantId: Long): Boolean {
    val criteria = Criteria.where("tenantId").`is`(tenantId)
    val query = Query.query(criteria)
    return mongoTemplate.exists(query, clazz).awaitSingle()
  }

  @Suppress("DuplicatedCode")
  override suspend fun updateAccountByUserId(userId: String, account: String): Long {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    val update = Update()
    update.set("account", account)
    return mongoTemplate.updateMulti(query, update, clazz).awaitSingle().modifiedCount
  }

  @Suppress("DuplicatedCode")
  override suspend fun updatePhoneByUserId(userId: String, phone: String): Long {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    val update = Update()
    update.set("phone", TenantUserDO.encrypt(phone))
    return mongoTemplate.updateMulti(query, update, clazz).awaitSingle().modifiedCount
  }

  @Suppress("DuplicatedCode")
  override suspend fun updateEmailByUserId(userId: String, email: String): Long {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    val update = Update()
    update.set("email", TenantUserDO.encrypt(email))
    return mongoTemplate.updateMulti(query, update, clazz).awaitSingle().modifiedCount
  }

}
