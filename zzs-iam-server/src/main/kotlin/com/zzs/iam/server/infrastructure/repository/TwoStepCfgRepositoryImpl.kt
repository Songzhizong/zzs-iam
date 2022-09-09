package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.twostep.TwoStepCfgDo
import com.zzs.iam.server.domain.model.twostep.TwoStepCfgRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/25
 */
@Repository
class TwoStepCfgRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : TwoStepCfgRepository {
  private val clazz = TwoStepCfgDo::class.java

  override suspend fun save(twoStepCfgDo: TwoStepCfgDo): TwoStepCfgDo {
    if (twoStepCfgDo.id < 1) {
      twoStepCfgDo.id = idGenerator.generate()
      return mongoTemplate.insert(twoStepCfgDo).awaitSingle()
    }
    return mongoTemplate.save(twoStepCfgDo).awaitSingle()
  }

  override suspend fun findByPlatformAndTenantId(platform: String, tenantId: Long): TwoStepCfgDo? {
    val criteria = Criteria.where("platform").`is`(platform).and("tenantId").`is`(tenantId)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }
}
