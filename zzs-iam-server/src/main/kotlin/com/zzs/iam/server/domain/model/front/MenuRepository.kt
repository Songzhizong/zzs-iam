package com.zzs.iam.server.domain.model.front

import com.zzs.iam.common.pojo.SimpleMenu

/**
 * @author 宋志宗 on 2022/8/15
 */
interface MenuRepository {

  suspend fun save(menuDo: MenuDO): MenuDO

  suspend fun saveAll(menus: Collection<MenuDO>): List<MenuDO>

  suspend fun delete(menuDo: MenuDO)

  suspend fun deleteAllChildByRouter(router: String): Long

  suspend fun findById(id: Long): MenuDO?

  suspend fun findAllById(ids: Collection<Long>): List<MenuDO>

  suspend fun findAllByTerminal(terminal: String): List<MenuDO>

  suspend fun findAllChild(parentRouter: String): List<MenuDO>

  suspend fun existsByParentId(parentId: Long): Boolean

  suspend fun findSimpleMenusById(ids: Collection<Long>): List<SimpleMenu>

  suspend fun findSimpleMenusByPlatform(platform: String): List<SimpleMenu>

  suspend fun findSimpleMenusByTerminal(terminal: String): List<SimpleMenu>

  suspend fun findApisById(ids: Collection<Long>): Set<String>

  suspend fun findApisByPlatform(platform: String): Set<String>
}
