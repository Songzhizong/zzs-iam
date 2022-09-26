package com.zzs.iam.server.infrastructure.repository

import com.zzs.iam.common.pojo.SimpleMenu
import com.zzs.iam.server.domain.model.front.MenuDO
import com.zzs.iam.server.domain.model.front.MenuRepository
import com.zzs.iam.server.infrastructure.IamIDGenerator
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * @author 宋志宗 on 2022/8/15
 */
@Repository
class MenuRepositoryImpl(
  private val idGenerator: IamIDGenerator,
  private val mongoTemplate: ReactiveMongoTemplate,
) : MenuRepository {
  private val menuDoClass = MenuDO::class.java
  private val simpleMenuClass = SimpleMenu::class.java
  private val simpleMenuFields = listOf(
    "id", "parentId", "terminal", "name", "type", "order", "icon", "selectedIcon", "url", "path"
  )

  override suspend fun save(menuDo: MenuDO): MenuDO {
    if (menuDo.id < 1) {
      menuDo.id = idGenerator.generate()
      return mongoTemplate.insert(menuDo).awaitSingle()
    }
    return mongoTemplate.save(menuDo).awaitSingle()
  }

  override suspend fun saveAll(menus: Collection<MenuDO>): List<MenuDO> {
    return Flux.fromIterable(menus).flatMap {
      if (it.id < 1) {
        it.id = idGenerator.generate()
        return@flatMap mongoTemplate.insert(it)
      }
      return@flatMap mongoTemplate.save(it)
    }.collectList().awaitSingle()
  }

  override suspend fun delete(menuDo: MenuDO) {
    mongoTemplate.remove(menuDo).awaitSingle()
  }

  override suspend fun deleteAllChildByRouter(router: String): Long {
    val criteria = Criteria.where("parentRouter").regex("^$router.*$")
    val query = Query.query(criteria)
    return mongoTemplate.remove(query, menuDoClass).awaitSingle().deletedCount
  }

  override suspend fun findById(id: Long): MenuDO? {
    val criteria = Criteria.where("id").`is`(id)
    val query = Query.query(criteria)
    return mongoTemplate.findOne(query, menuDoClass).awaitSingleOrNull()
  }

  override suspend fun findAllById(ids: Collection<Long>): List<MenuDO> {
    val criteria = Criteria.where("ids").`in`(ids)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, menuDoClass).collectList().awaitSingle()
  }

  override suspend fun findAllByTerminal(terminal: String): List<MenuDO> {
    val criteria = Criteria.where("terminal").`is`(terminal)
    val query = Query.query(criteria)
    return mongoTemplate.find(query, menuDoClass).collectList().awaitSingle()
  }

  override suspend fun findAllChild(parentRouter: String): List<MenuDO> {
    val criteria = Criteria.where("parentRouter").regex("^$parentRouter.*$")
    val query = Query.query(criteria)
    return mongoTemplate.find(query, menuDoClass).collectList().awaitSingle()
  }

  override suspend fun existsByParentId(parentId: Long): Boolean {
    val criteria = Criteria.where("parentId").`is`(parentId)
    val query = Query.query(criteria)
    return mongoTemplate.exists(query, menuDoClass).awaitSingle()
  }

  override suspend fun findSimpleMenusById(ids: Collection<Long>): List<SimpleMenu> {
    val criteria = Criteria.where("ids").`in`(ids)
    val query = Query.query(criteria)
    val findFields = query.fields()
    simpleMenuFields.forEach { findFields.include(it) }
    return mongoTemplate.find(query, simpleMenuClass).collectList().awaitSingle()
  }

  override suspend fun findSimpleMenusByPlatform(platform: String): List<SimpleMenu> {
    val criteria = Criteria.where("platform").`is`(platform)
    val query = Query.query(criteria)
    val findFields = query.fields()
    simpleMenuFields.forEach { findFields.include(it) }
    return mongoTemplate.find(query, simpleMenuClass).collectList().awaitSingle()
  }

  override suspend fun findSimpleMenusByTerminal(terminal: String): List<SimpleMenu> {
    val criteria = Criteria.where("terminal").`is`(terminal)
    val query = Query.query(criteria)
    val findFields = query.fields()
    simpleMenuFields.forEach { findFields.include(it) }
    return mongoTemplate.find(query, simpleMenuClass).collectList().awaitSingle()
  }

  @Suppress("DuplicatedCode")
  override suspend fun findApisById(ids: Collection<Long>): Set<String> {
    val criteria = Criteria.where("ids").`in`(ids)
    val query = Query.query(criteria)
    val findFields = query.fields()
    findFields.include("apis")
    val awaitSingle = mongoTemplate.find(query, menuDoClass).collectList().awaitSingle()
    val apis = HashSet<String>()
    for (menuDo in awaitSingle) {
      apis.addAll(menuDo.apis)
    }
    return apis
  }

  @Suppress("DuplicatedCode")
  override suspend fun findApisByPlatform(platform: String): Set<String> {
    val criteria = Criteria.where("platform").`is`(platform)
    val query = Query.query(criteria)
    val findFields = query.fields()
    findFields.include("apis")
    val awaitSingle = mongoTemplate.find(query, menuDoClass).collectList().awaitSingle()
    val apis = HashSet<String>()
    for (menuDo in awaitSingle) {
      apis.addAll(menuDo.apis)
    }
    return apis
  }
}
