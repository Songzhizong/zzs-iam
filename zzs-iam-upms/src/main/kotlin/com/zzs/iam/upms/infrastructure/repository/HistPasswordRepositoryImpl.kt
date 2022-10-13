package com.zzs.iam.upms.infrastructure.repository

import com.zzs.iam.upms.domain.model.user.HistPasswordDO
import com.zzs.iam.upms.domain.model.user.HistPasswordRepository
import com.zzs.iam.upms.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/8/23
 */
@Repository
class HistPasswordRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : HistPasswordRepository {
  private val clazz = HistPasswordDO::class.java

  override suspend fun save(histPasswordDo: HistPasswordDO): HistPasswordDO {
    if (histPasswordDo.id < 1) {
      histPasswordDo.id = idGenerator.generate()
      return mongoTemplate.insert(histPasswordDo).awaitSingle()
    }
    return mongoTemplate.save(histPasswordDo).awaitSingle()
  }

  override suspend fun findUserLatest(userId: Long, count: Int): List<HistPasswordDO> {
    val pageable = PageRequest.of(0, count, Sort.Direction.DESC, "id")
    val criteria = Criteria.where("userId").`is`(userId)
    val query = Query.query(criteria).with(pageable)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }

}
