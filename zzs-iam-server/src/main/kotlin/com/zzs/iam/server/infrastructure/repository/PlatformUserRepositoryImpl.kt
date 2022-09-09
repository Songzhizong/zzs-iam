package com.zzs.iam.server.infrastructure.repository

import com.zzs.framework.core.spring.toPageable
import com.zzs.iam.server.domain.model.org.PlatformUserDo
import com.zzs.iam.server.domain.model.org.PlatformUserRepository
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
class PlatformUserRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : PlatformUserRepository {
  private val clazz = PlatformUserDo::class.java

  override suspend fun save(platformUserDo: PlatformUserDo): PlatformUserDo {
    if (platformUserDo.id < 1) {
      platformUserDo.id = idGenerator.generate()
      return mongoTemplate.insert(platformUserDo).awaitSingle()
    }
    return mongoTemplate.save(platformUserDo).awaitSingle()
  }

  override suspend fun saveAll(platformUsers: Collection<PlatformUserDo>) {
    platformUsers.forEach {
      if (it.id < 1) {
        it.id = idGenerator.generate()
      }
    }
    Flux.fromIterable(platformUsers).flatMap { mongoTemplate.save(it) }.awaitLast()
  }

  override suspend fun delete(platformUserDo: PlatformUserDo) {
    mongoTemplate.remove(platformUserDo).awaitSingle()
  }

  override suspend fun deleteAll(platformUsers: Collection<PlatformUserDo>) {
    val ids = platformUsers.map { it.id }.toTypedArray()
    val criteria = Criteria.where("id").`in`(*ids)
    val query = Query.query(criteria)
    mongoTemplate.remove(query, clazz).awaitSingle()
  }

  override suspend fun findById(id: Long): PlatformUserDo? {
    val criteria = Criteria.where("id").`is`(id)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findByPlatformAndUserId(platform: String, userId: String): PlatformUserDo? {
    val criteria = Criteria.where("platform").`is`(platform).and("userId").`is`(userId)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findByPlatformAndUserIdIn(
    platform: String,
    userIds: Collection<String>
  ): List<PlatformUserDo> {
    val criteria = Criteria.where("platform").`is`(platform).and("userId").`in`(userIds)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAllByUserId(userId: String): List<PlatformUserDo> {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  @Suppress("DuplicatedCode")
  override suspend fun query(platform: String, args: QueryUserArgs): Page<PlatformUserDo> {
    val paging = args.paging.descBy("id")
    val uniqueIdent = args.uniqueIdent?.ifBlank { null }
    val criteria = Criteria.where("platform").`is`(platform)
    uniqueIdent?.also {
      val encrypt = PlatformUserDo.encrypt(it)
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
    update.set("phone", PlatformUserDo.encrypt(phone))
    return mongoTemplate.updateMulti(query, update, clazz).awaitSingle().modifiedCount
  }

  @Suppress("DuplicatedCode")
  override suspend fun updateEmailByUserId(userId: String, email: String): Long {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    val update = Update()
    update.set("email", PlatformUserDo.encrypt(email))
    return mongoTemplate.updateMulti(query, update, clazz).awaitSingle().modifiedCount
  }
}
