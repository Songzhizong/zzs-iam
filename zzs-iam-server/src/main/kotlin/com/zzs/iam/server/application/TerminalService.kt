package com.zzs.iam.server.application

import com.zzs.framework.core.exception.BadRequestException
import com.zzs.framework.core.exception.ResourceNotFoundException
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.iam.server.domain.model.front.TerminalDo
import com.zzs.iam.server.domain.model.front.TerminalRepository
import com.zzs.iam.server.domain.model.org.PlatformRepository
import com.zzs.iam.server.dto.args.CreateTerminalArgs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author 宋志宗 on 2022/8/16
 */
@Service
@Transactional(rollbackFor = [Throwable::class])
class TerminalService(
  private val platformRepository: PlatformRepository,
  private val terminalRepository: TerminalRepository,
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(TerminalService::class.java)
  }

  /** 新增终端 */
  suspend fun create(args: CreateTerminalArgs): TerminalDo {
    val platform = args.platform.requireNotBlank { "平台编码为空" }.also {
      platformRepository.findByCode(it) ?: kotlin.run {
        log.info("平台: {} 不存在", it)
        throw ResourceNotFoundException("平台不存在")
      }
    }
    val code = args.code.requireNotBlank { "终端编码为空" }.also {
      terminalRepository.findByCode(it)?.apply {
        log.info("重点编码: {} 已被使用", it)
        throw BadRequestException("终端编码已被使用")
      }
    }
    val name = args.name.requireNotBlank { "终端名称为空" }
    val note = args.note
    val terminalDo = TerminalDo.create(code, platform, name, note)
    terminalRepository.save(terminalDo)
    return terminalDo
  }

  /** 修改终端信息 */
  suspend fun update(code: String, name: String, note: String?): TerminalDo {
    val terminalDo = terminalRepository.findByCode(code) ?: let {
      log.info("修改终端信息失败, 终端: {} 不存在", code)
      throw ResourceNotFoundException("终端不存在")
    }
    terminalDo.name = name
    terminalDo.note = note
    terminalRepository.save(terminalDo)
    return terminalDo
  }

  /** 删除终端 */
  suspend fun delete(code: String) {
    terminalRepository.findByCode(code)?.also {
      terminalRepository.delete(it)
      log.info("成功删除终端: [{} {}]", code, it.name)
    } ?: let {
      log.info("终端: {} 不存在", code)
    }
  }
}
