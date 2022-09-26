package com.zzs.iam.server.domain.model.twostep

/**
 * @author 宋志宗 on 2022/8/25
 */
interface TwoStepCfgRepository {

  suspend fun save(twoStepCfgDo: TwoStepCfgDO): TwoStepCfgDO

  suspend fun findByPlatformAndTenantId(platform: String, tenantId: Long): TwoStepCfgDO?
}
