package com.zzs.iam.server.domain.model.front

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TerminalRepository {

  suspend fun save(terminalDo: TerminalDO): TerminalDO

  suspend fun delete(terminalDo: TerminalDO)

  suspend fun findByCode(code: String): TerminalDO?

  suspend fun findAllByPlatform(platform: String): List<TerminalDO>
}
