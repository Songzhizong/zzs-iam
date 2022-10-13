package com.zzs.iam.upms.infrastructure

import cn.idealframework2.trace.OperationLog
import cn.idealframework2.trace.reactive.OperationLogStore
import com.zzs.iam.upms.domain.model.log.OperationLogDO
import com.zzs.iam.upms.domain.model.log.OperationLogRepository
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