package com.zzs.iam.server.configure

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author 宋志宗 on 2022/8/15
 */
@EnableScheduling
@ComponentScan("com.zzs.iam.server")
@EntityScan("com.zzs.iam.server.domain.model")
@EnableReactiveMongoRepositories("com.zzs.iam.server")
@EnableConfigurationProperties(IamUpmsProperties::class)
class IamServerAutoConfigure
