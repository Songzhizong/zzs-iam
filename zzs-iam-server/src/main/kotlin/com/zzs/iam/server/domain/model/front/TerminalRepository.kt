package com.zzs.iam.server.domain.model.front

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TerminalRepository {

  suspend fun save(terminalDo: TerminalDo): TerminalDo

  suspend fun delete(terminalDo: TerminalDo)

  suspend fun findByCode(code: String): TerminalDo?

  suspend fun findAllByPlatform(platform: String): List<TerminalDo>
}
