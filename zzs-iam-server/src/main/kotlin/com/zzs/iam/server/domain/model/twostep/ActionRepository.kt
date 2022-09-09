package com.zzs.iam.server.domain.model.twostep

/**
 * @author 宋志宗 on 2022/8/25
 */
interface ActionRepository {

  suspend fun save(actionDo: ActionDo): ActionDo

  suspend fun findAllByPlatform(platform: String): List<ActionDo>

  suspend fun findAllEnabledByPlatform(platform: String): List<ActionDo>
}
