package com.zzs.iam.server.domain.model.org

import com.zzs.iam.server.dto.args.QueryTenantArgs
import org.springframework.data.domain.Page

/**
 * @author 宋志宗 on 2022/8/15
 */
interface TenantRepository {

  suspend fun save(tenantDo: com.zzs.iam.server.domain.model.org.TenantDo): com.zzs.iam.server.domain.model.org.TenantDo

  suspend fun delete(tenantDo: com.zzs.iam.server.domain.model.org.TenantDo)

  suspend fun findById(id: Long): com.zzs.iam.server.domain.model.org.TenantDo?

  suspend fun findAllById(ids: Collection<Long>): List<com.zzs.iam.server.domain.model.org.TenantDo>

  suspend fun findAllChild(parentRouter: String): List<com.zzs.iam.server.domain.model.org.TenantDo>

  suspend fun existsByParentId(parentId: Long): Boolean

  suspend fun query(platform: String, args: com.zzs.iam.server.dto.args.QueryTenantArgs): Page<com.zzs.iam.server.domain.model.org.TenantDo>
}
