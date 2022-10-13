package com.zzs.iam.server.domain.model.log

import cn.idealframework2.transmission.Paging
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/9/24
 */
interface LoginLogRepository {

  suspend fun save(loginLog: LoginLogDO): LoginLogDO

  suspend fun findByPlatformAndUserId(
    platform: String,
    userId: String,
    paging: Paging
  ): Page<LoginLogDO>
}
