package com.zzs.iam.server.domain.model.user

/**
 * @author 宋志宗 on 2022/8/23
 */
interface HistPasswordRepository {

  suspend fun save(histPasswordDo: com.zzs.iam.server.domain.model.user.HistPasswordDo): com.zzs.iam.server.domain.model.user.HistPasswordDo

  /** 获取用户最近一定次数使用过的密码 */
  suspend fun findUserLatest(userId: Long, count: Int): List<com.zzs.iam.server.domain.model.user.HistPasswordDo>
}
