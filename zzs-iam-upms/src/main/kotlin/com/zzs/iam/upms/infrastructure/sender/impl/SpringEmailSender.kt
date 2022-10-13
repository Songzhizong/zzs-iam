package com.zzs.iam.upms.infrastructure.sender.impl

import cn.idealframework2.trace.coroutine.TraceContextHolder
import cn.idealframework2.utils.CommonPool
import com.zzs.iam.upms.infrastructure.sender.EmailSender
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

/**
 * @author 宋志宗 on 2022/8/26
 */
@Component
@ImportRuntimeHints(SpringEmailSender.SpringEmailSenderRuntimeHints::class)
class SpringEmailSender(
  private val javaMailSender: JavaMailSender
) : EmailSender {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(SpringEmailSender::class.java)
  }

  @Value("\${spring.mail.username:}")
  var form: String = ""

  private val codeTemplate: String?

  init {
    val inputStream: InputStream? =
      Thread.currentThread().contextClassLoader.getResourceAsStream("template/mail_code.html")
    if (inputStream != null) {
      inputStream.use {
        InputStreamReader(inputStream).use { isr ->
          BufferedReader(isr).use { br ->
            val sb = StringBuilder()
            br.lines().forEach { sb.append(it) }
            codeTemplate = sb.toString()
          }
        }
      }
    } else {
      log.error("验证码邮件模板为空")
      codeTemplate = null
    }
  }

  override suspend fun sendMailCode(email: String, operation: String, code: String) {
    val logPrefix = TraceContextHolder.awaitLogPrefix()
    val start = System.currentTimeMillis()
    if (codeTemplate.isNullOrBlank()) {
      return
    }
    val content = codeTemplate.replace("{{.code}}", code)
      .replace("{{.operation}}", operation)
    val message = javaMailSender.createMimeMessage()
    val helper = MimeMessageHelper(message)
    helper.setFrom(form)
    helper.setTo(email)
    helper.setSubject("IAM验证码")
    helper.setText(content, true)
    Mono.fromFuture(
      CompletableFuture.supplyAsync(
        { javaMailSender.send(message);true },
        CommonPool.INSTANCE
      )
    ).awaitSingleOrNull()
    log.debug("{}邮件发送耗时: {}", logPrefix, System.currentTimeMillis() - start)
  }

  class SpringEmailSenderRuntimeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
      hints.resources().registerPattern("template/mail_code.html")
    }
  }
}
