package com.zzs.iam.server.domain.model.front

import com.zzs.iam.common.pojo.SimpleMenu

/**
 * @author 宋志宗 on 2022/8/15
 */
interface MenuRepository {

  suspend fun save(menuDo: MenuDo): MenuDo

  suspend fun saveAll(menus: Collection<MenuDo>): List<MenuDo>

  suspend fun delete(menuDo: MenuDo)

  suspend fun deleteAllChildByRouter(router: String): Long

  suspend fun findById(id: Long): MenuDo?

  suspend fun findAllById(ids: Collection<Long>): List<MenuDo>

  suspend fun findAllByTerminal(terminal: String): List<MenuDo>

  suspend fun findAllChild(parentRouter: String): List<MenuDo>

  suspend fun existsByParentId(parentId: Long): Boolean

  suspend fun findSimpleMenusById(ids: Collection<Long>): List<SimpleMenu>

  suspend fun findSimpleMenusByPlatform(platform: String): List<SimpleMenu>

  suspend fun findSimpleMenusByTerminal(terminal: String): List<SimpleMenu>

  suspend fun findApisById(ids: Collection<Long>): Set<String>

  suspend fun findApisByPlatform(platform: String): Set<String>
}
