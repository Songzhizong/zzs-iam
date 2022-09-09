package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.user.UserDo
import com.zzs.iam.server.domain.model.user.UserRepository
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
class UserRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : UserRepository {

  override suspend fun save(userDo: UserDo): UserDo {
    if (userDo.id < 1) {
      userDo.id = idGenerator.generate()
      return mongoTemplate.insert(userDo).awaitSingle()
    }
    return mongoTemplate.save(userDo).awaitSingle()
  }

  override suspend fun findById(id: Long): UserDo? {
    val criteria = Criteria.where("id").`is`(id)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Collection<Long>): List<UserDo> {
    val criteria = Criteria.where("id").`in`(ids)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, UserDo::class.java).collectList().awaitSingle()
  }

  override suspend fun findByPhone(phone: String): UserDo? {
    val encrypt = UserDo.encrypt(phone)
    val criteria = Criteria.where("phone").`is`(encrypt)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, UserDo::class.java).awaitSingleOrNull()
  }

  override suspend fun findByUniqueIdent(uniqueIdent: String): UserDo? {
    val encrypt = UserDo.encrypt(uniqueIdent)
    val criteria = Criteria().orOperator(
      Criteria.where("account").`is`(uniqueIdent),
      Criteria.where("email").`is`(encrypt),
      Criteria.where("phone").`is`(encrypt)
    )
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, UserDo::class.java).awaitSingleOrNull()
  }

}
