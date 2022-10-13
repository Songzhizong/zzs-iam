package com.zzs.iam.upms.domain.model.org

import com.zzs.iam.upms.dto.args.QueryTenantArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TenantRepository {

  suspend fun save(tenantDo: TenantDO): TenantDO

  suspend fun delete(tenantDo: TenantDO)

  suspend fun findById(id: Long): TenantDO?

  suspend fun findAllById(ids: Collection<Long>): List<TenantDO>

  suspend fun findAllChild(parentRouter: String): List<TenantDO>

  suspend fun existsByParentId(parentId: Long): Boolean

  suspend fun query(platform: String, args: QueryTenantArgs): Page<TenantDO>
}
