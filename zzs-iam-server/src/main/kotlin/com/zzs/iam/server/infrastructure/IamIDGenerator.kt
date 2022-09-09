package com.zzs.iam.server.infrastructure

import com.zzs.framework.core.id.IDGenerator
import com.zzs.framework.core.id.IDGeneratorFactory
import org.springframework.stereotype.Component

/**
 * @author 宋志宗 on 2022/8/14
 */
@Component
class IamIDGenerator(idGeneratorFactory: IDGeneratorFactory) : IDGenerator {
  private val idGenerator: IDGenerator

  init {
    idGenerator = idGeneratorFactory.getGenerator("iam")
  }

  override fun generate() = idGenerator.generate()
}
