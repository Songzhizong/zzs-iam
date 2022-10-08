package com.zzs.iam.server.infrastructure

import cn.idealframework2.id.IDGenerator
import cn.idealframework2.id.IDGeneratorFactory
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
