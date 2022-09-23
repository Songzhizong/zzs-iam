package com.zzs.iam.server.infrastructure

import com.zzs.framework.core.trace.OperationLog
import com.zzs.framework.core.trace.reactive.OperationLogStore
import com.zzs.iam.server.domain.model.log.OperationLogDO
import com.zzs.iam.server.domain.model.log.OperationLogRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * @author 宋志宗 on 2022/9/23
 */
@Repository
class IamOperationLogStore(
  private val operationLogRepository: OperationLogRepository
) : OperationLogStore {

  override fun save(operationLog: OperationLog): Mono<Boolean> {
    return mono {
      val operationLogDo = OperationLogDO.create(operationLog)
      operationLogRepository.save(operationLogDo)
      true
    }
  }
}
