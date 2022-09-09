package com.zzs.iam.server.domain.model.twostep

/**
 * @author 宋志宗 on 2022/8/25
 */
interface TwoStepCfgRepository {

  suspend fun save(twoStepCfgDo: com.zzs.iam.server.domain.model.twostep.TwoStepCfgDo): com.zzs.iam.server.domain.model.twostep.TwoStepCfgDo

  suspend fun findByPlatformAndTenantId(platform: String, tenantId: Long): com.zzs.iam.server.domain.model.twostep.TwoStepCfgDo?
}
