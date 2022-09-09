package com.zzs.iam.server.domain.model.user.event.builder

import com.zzs.framework.core.event.Event
import com.zzs.framework.core.event.EventBuilder
import com.zzs.iam.common.event.user.UserCreated
import com.zzs.iam.server.domain.model.user.UserDo

/**
 * @author 宋志宗 on 2022/8/15
 */
class UserCreatedBuilder(private val userDo: com.zzs.iam.server.domain.model.user.UserDo) : EventBuilder {

  override fun build(): Event {
    val user = userDo.toUser()
    return UserCreated().also { it.user = user }
  }
}
