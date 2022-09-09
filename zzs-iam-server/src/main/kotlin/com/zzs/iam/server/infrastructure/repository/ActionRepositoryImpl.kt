package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.twostep.ActionDo
import com.zzs.iam.server.domain.model.twostep.ActionRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/25
 */
@Repository
class ActionRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : ActionRepository {
  private val clazz = ActionDo::class.java

  override suspend fun save(actionDo: ActionDo): ActionDo {
    if (actionDo.id < 1) {
      actionDo.id = idGenerator.generate()
      return mongoTemplate.insert(actionDo).awaitSingle()
    }
    return mongoTemplate.save(actionDo).awaitSingle()
  }

  override suspend fun findAllByPlatform(platform: String): List<ActionDo> {
    val criteria = Criteria.where("platform").`is`(platform)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

  override suspend fun findAllEnabledByPlatform(platform: String): List<ActionDo> {
    val criteria = Criteria.where("platform").`is`(platform)
      .and("enabled").`is`(true)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

}
