package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.front.TerminalDO
import com.zzs.iam.server.domain.model.front.TerminalRepository
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
class TerminalRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : TerminalRepository {
  private val clazz = TerminalDO::class.java

  override suspend fun save(terminalDo: TerminalDO): TerminalDO {
    if (terminalDo.id < 1) {
      terminalDo.id = idGenerator.generate()
      return mongoTemplate.insert(terminalDo).awaitSingle()
    }
    return mongoTemplate.save(terminalDo).awaitSingle()
  }

  override suspend fun delete(terminalDo: TerminalDO) {
    mongoTemplate.remove(terminalDo).awaitSingle()
  }

  override suspend fun findByCode(code: String): TerminalDO? {
    val criteria = Criteria.where("code").`is`(code)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, clazz).awaitSingleOrNull()
  }

  override suspend fun findAllByPlatform(platform: String): List<TerminalDO> {
    val criteria = Criteria.where("platform").`is`(platform)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, clazz).collectList().awaitSingle()
  }
}
