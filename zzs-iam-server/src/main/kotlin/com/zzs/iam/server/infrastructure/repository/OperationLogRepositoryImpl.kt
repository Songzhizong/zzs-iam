package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.server.domain.model.log.OperationLogDo
import com.zzs.iam.server.domain.model.log.OperationLogRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/9/23
 */
@Repository
class OperationLogRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : OperationLogRepository {

  override suspend fun save(log: OperationLogDo): OperationLogDo {
    if (log.id < 1) {
      log.id = idGenerator.generate()
      return mongoTemplate.insert(log).awaitSingle()
    }
    return mongoTemplate.save(log).awaitSingle()
  }

}
