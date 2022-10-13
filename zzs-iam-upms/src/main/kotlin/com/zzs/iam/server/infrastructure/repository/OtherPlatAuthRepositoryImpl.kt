package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.user.OtherPlatAuthDO
import com.zzs.iam.server.domain.model.user.OtherPlatAuthRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/24
 */
@Repository
class OtherPlatAuthRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : OtherPlatAuthRepository {
  private val clazz = OtherPlatAuthDO::class.java

  override suspend fun save(otherPlatAuthDo: OtherPlatAuthDO): OtherPlatAuthDO {
    if (otherPlatAuthDo.id < 1) {
      otherPlatAuthDo.id = idGenerator.generate()
      return mongoTemplate.insert(otherPlatAuthDo).awaitSingle()
    }
    return mongoTemplate.save(otherPlatAuthDo).awaitSingle()
  }

  override suspend fun delete(otherPlatAuthDo: OtherPlatAuthDO) {
    mongoTemplate.remove(otherPlatAuthDo).awaitSingle()
  }

  override suspend fun deleteAllByUserId(userId: Long) {
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria)
    mongoTemplate.remove(query, clazz).awaitSingle()
  }

  override suspend fun deleteByUserIdAndPlatCode(userId: Long, platCode: String) {
    val criteria = Criteria.where("userId").`is`(userId)
      .and("platCode").`is`(platCode)
    val query = Query.query(criteria)
    mongoTemplate.remove(query, clazz).awaitSingle()
  }

  override suspend fun findByPlatCodeAndOtherPlatUserId(
    platCode: String,
    otherPlatUserId: String
  ): OtherPlatAuthDO? {
    val criteria = Criteria.where("platCode").`is`(platCode)
      .and("otherPlatUserId").`is`(otherPlatUserId)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingle()
  }

  override suspend fun findByUserIdAndPlatCode(userId: Long, platCode: String): OtherPlatAuthDO? {
    val criteria = Criteria.where("userId").`is`(userId)
      .and("platCode").`is`(platCode)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingle()
  }
}
