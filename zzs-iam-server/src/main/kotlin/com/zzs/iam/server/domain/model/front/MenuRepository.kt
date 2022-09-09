package com.zzs.iam.server.domain.model.front

import com.zzs.iam.common.pojo.SimpleMenu

/**
 * @author 宋志宗 on 2022/8/15
 */
interface MenuRepository {

  suspend fun save(menuDo: com.zzs.iam.server.domain.model.front.MenuDo): com.zzs.iam.server.domain.model.front.MenuDo

  suspend fun saveAll(menus: Collection<com.zzs.iam.server.domain.model.front.MenuDo>): List<com.zzs.iam.server.domain.model.front.MenuDo>

  suspend fun delete(menuDo: com.zzs.iam.server.domain.model.front.MenuDo)

  suspend fun deleteAllChildByRouter(router: String): Long

  suspend fun findById(id: Long): com.zzs.iam.server.domain.model.front.MenuDo?

  suspend fun findAllById(ids: Collection<Long>): List<com.zzs.iam.server.domain.model.front.MenuDo>

  suspend fun findAllByTerminal(terminal: String): List<com.zzs.iam.server.domain.model.front.MenuDo>

  suspend fun findAllChild(parentRouter: String): List<com.zzs.iam.server.domain.model.front.MenuDo>

  suspend fun existsByParentId(parentId: Long): Boolean

  suspend fun findSimpleMenusById(ids: Collection<Long>): List<SimpleMenu>

  suspend fun findSimpleMenusByPlatform(platform: String): List<SimpleMenu>

  suspend fun findSimpleMenusByTerminal(terminal: String): List<SimpleMenu>

  suspend fun findApisById(ids: Collection<Long>): Set<String>

  suspend fun findApisByPlatform(platform: String): Set<String>
}
