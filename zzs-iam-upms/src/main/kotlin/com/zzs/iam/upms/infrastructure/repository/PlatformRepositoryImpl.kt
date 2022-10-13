package com.zzs.iam.upms.infrastructure.repository

import com.zzs.iam.upms.domain.model.org.PlatformDO
import com.zzs.iam.upms.domain.model.org.PlatformRepository
import com.zzs.iam.upms.infrastructure.IamIDGenerator
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
class PlatformRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : PlatformRepository {
  private val clazz = PlatformDO::class.java

  override suspend fun save(platformDo: PlatformDO): PlatformDO {
    if (platformDo.id < 1) {
      platformDo.id = idGenerator.generate()
      return mongoTemplate.insert(platformDo).awaitSingle()
    }
    return mongoTemplate.save(platformDo).awaitSingle()
  }

  override suspend fun delete(platformDo: PlatformDO) {
    mongoTemplate.remove(platformDo).awaitSingle()
  }

  override suspend fun findByCode(code: String): PlatformDO? {
    val criteria = Criteria.where("code").`is`(code)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findAll(): List<PlatformDO> {
    return mongoTemplate.findAll(clazz).collectList().awaitSingle()
  }
}
