package com.zzs.iam.server.infrastructure.repository

import cn.idealframework2.spring.toPageable
import cn.idealframework2.transmission.Paging
import com.zzs.iam.server.domain.model.log.LoginLogDO
import com.zzs.iam.server.domain.model.log.LoginLogRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

/**
 * @author 宋志宗 on 2022/9/24
 */
@Repository
class LoginLogRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : LoginLogRepository {
  private val clazz = LoginLogDO::class.java

  override suspend fun save(loginLog: LoginLogDO): LoginLogDO {
    if (loginLog.id < 1) {
      loginLog.id = idGenerator.generate()
      return mongoTemplate.insert(loginLog).awaitSingle()
    }
    return mongoTemplate.save(loginLog).awaitSingle()
  }

  @Suppress("DuplicatedCode")
  override suspend fun findByPlatformAndUserId(
    platform: String,
    userId: String,
    paging: Paging
  ): Page<LoginLogDO> {
    val sortablePaging = paging.descBy("id")
    val criteria = Criteria.where("platform").`is`(platform)
      .and("userId").`is`(userId)
    val pageable = sortablePaging.toPageable()
    val query = Query.query(criteria).with(pageable)
    val content = mongoTemplate.find(query, clazz).collectList().awaitSingle()
    if (content.size < pageable.pageSize) {
      return PageImpl(content, pageable, 0)
    }
    val total = mongoTemplate.count(Query.query(criteria), clazz).awaitSingle()
    return PageImpl(content, pageable, total)
  }
}
