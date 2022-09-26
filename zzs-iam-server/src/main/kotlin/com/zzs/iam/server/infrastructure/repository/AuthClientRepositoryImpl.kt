package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.org.AuthClientDO
import com.zzs.iam.server.domain.model.org.AuthClientRepository
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
class AuthClientRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : AuthClientRepository {
  private val clazz = AuthClientDO::class.java

  override suspend fun save(authClientDo: AuthClientDO): AuthClientDO {
    if (authClientDo.id < 1) {
      authClientDo.id = idGenerator.generate()
      return mongoTemplate.insert(authClientDo).awaitSingle()
    }
    return mongoTemplate.save(authClientDo).awaitSingle()
  }

  override suspend fun delete(authClientDo: AuthClientDO) {
    mongoTemplate.remove(authClientDo).awaitSingle()
  }

  override suspend fun findByClientId(clientId: String): AuthClientDO? {
    val criteria = Criteria.where("clientId").`is`(clientId)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findAllByPlatform(platform: String): List<AuthClientDO> {
    val criteria = Criteria.where("platform").`is`(platform)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAllByPlatformIn(platforms: Collection<String>): List<AuthClientDO> {
    val criteria = Criteria.where("platform").`in`(platforms)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

}
