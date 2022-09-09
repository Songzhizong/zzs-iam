package com.zzs.iam.server.domain.model.front

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TerminalRepository {

  suspend fun save(terminalDo: com.zzs.iam.server.domain.model.front.TerminalDo): com.zzs.iam.server.domain.model.front.TerminalDo

  suspend fun delete(terminalDo: com.zzs.iam.server.domain.model.front.TerminalDo)

  suspend fun findByCode(code: String): com.zzs.iam.server.domain.model.front.TerminalDo?

  suspend fun findAllByPlatform(platform: String): List<com.zzs.iam.server.domain.model.front.TerminalDo>
}
