package com.zzs.iam.server.domain.model.log

/**
 * @author 宋志宗 on 2022/9/23
 */
interface OperationLogRepository {

  suspend fun save(log: OperationLogDO): OperationLogDO
}
