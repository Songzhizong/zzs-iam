package com.zzs.iam.server.domain.model.user.event.builder

import cn.idealframework2.event.Event
import cn.idealframework2.event.EventBuilder
import com.zzs.iam.common.event.user.UserCreated
import com.zzs.iam.server.domain.model.user.UserDO

/**
 * @author 宋志宗 on 2022/8/15
 */
class UserCreatedBuilder(private val userDo: UserDO) : EventBuilder {

  override fun build(): Event {
    val user = userDo.toUser()
    return UserCreated().also { it.user = user }
  }
}
