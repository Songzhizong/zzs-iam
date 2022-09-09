package com.zzs.iam.server.domain.model.org

import com.zzs.iam.server.dto.args.QueryTenantArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TenantRepository {

  suspend fun save(tenantDo: TenantDo): TenantDo

  suspend fun delete(tenantDo: TenantDo)

  suspend fun findById(id: Long): TenantDo?

  suspend fun findAllById(ids: Collection<Long>): List<TenantDo>

  suspend fun findAllChild(parentRouter: String): List<TenantDo>

  suspend fun existsByParentId(parentId: Long): Boolean

  suspend fun query(platform: String, args: QueryTenantArgs): Page<TenantDo>
}
