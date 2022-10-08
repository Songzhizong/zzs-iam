package com.zzs.iam.server.domain.model.org.event.builder

import cn.idealframework2.event.Event
import cn.idealframework2.event.EventBuilder
import com.zzs.iam.common.event.platform.PlatformCreated
import com.zzs.iam.server.domain.model.org.PlatformDO

/**
 * @author 宋志宗 on 2022/8/17
 */
class PlatformCreatedBuilder(private val platformDo: PlatformDO) : EventBuilder {

  override fun build(): Event {
    val platform = platformDo.toPlatform()
    return PlatformCreated()
      .also { it.platform = platform }
  }
}
