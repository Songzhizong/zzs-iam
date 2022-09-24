package com.zzs.iam.server.port.http

import com.zzs.framework.core.trace.Operation
import com.zzs.framework.core.transmission.ListResult
import com.zzs.framework.core.transmission.Result
import com.zzs.framework.core.utils.requireNonnull
import com.zzs.framework.core.utils.requireNotBlank
import com.zzs.framework.core.utils.toTreeList
import com.zzs.iam.common.pojo.Menu
import com.zzs.iam.server.application.MenuService
import com.zzs.iam.server.domain.model.front.MenuRepository
import com.zzs.iam.server.dto.args.CreateMenuArgs
import com.zzs.iam.server.dto.args.UpdateMenuArgs
import com.zzs.iam.server.dto.resp.MenuTree
import org.springframework.web.bind.annotation.*

/**
 * 菜单管理
 *
 * @author 宋志宗 on 2022/8/16
 */
@RestController
@RequestMapping("/iam/menu")
class MenuController(
  private val menuService: MenuService,
  private val menuRepository: MenuRepository
) {

  /**
   * 新建菜单
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/menu/create_menu
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "terminal": "iam_web",
   *     "parentId": 393403253447983104,
   *     "name": "屏蔽规则",
   *     "type": "MENU",
   *     "order": null,
   *     "icon": "",
   *     "selectedIcon": "",
   *     "url": "",
   *     "path": "",
   *     "apis": [
   *       ""
   *     ]
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "393403444112654336",
   *       "parentId": "393403253447983104",
   *       "platform": "iam",
   *       "terminal": "iam_web",
   *       "name": "屏蔽规则",
   *       "type": "MENU",
   *       "order": 0,
   *       "icon": "",
   *       "selectedIcon": "",
   *       "url": "",
   *       "path": "",
   *       "apis": [
   *         ""
   *       ],
   *       "createdTime": "2022-08-23 23:51:28.048",
   *       "updatedTime": "2022-08-23 23:51:28.048"
   *     }
   *   }
   * </pre>
   */
  @Operation("新增菜单")
  @PostMapping("/create_menu")
  suspend fun create(@RequestBody args: CreateMenuArgs): Result<Menu> {
    val menuDo = menuService.create(args)
    val menu = menuDo.toMenu()
    return Result.success(menu)
  }

  /**
   * 修改菜单信息
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/menu/update_menu?id=393403096727814144
   *   Content-Type: application/json
   *   Authorization: {{token_type}} {{access_token}}
   *
   *   {
   *     "name": "资源",
   *     "type": "MENU",
   *     "order": null,
   *     "icon": "",
   *     "selectedIcon": "",
   *     "url": "",
   *     "path": "",
   *     "apis": [
   *     ]
   *   }
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "393403096727814144",
   *       "parentId": "-1",
   *       "platform": "iam",
   *       "terminal": "iam_web",
   *       "name": "资源",
   *       "type": "MENU",
   *       "order": 0,
   *       "icon": "",
   *       "selectedIcon": "",
   *       "url": "",
   *       "path": "",
   *       "apis": [],
   *       "createdTime": "2022-08-23 23:50:05.226",
   *       "updatedTime": "2022-08-23 23:54:14.410"
   *     }
   *   }
   * </pre>
   *
   * @param id 菜单ID
   */
  @Operation("更新菜单")
  @PostMapping("/update_menu")
  suspend fun update(id: Long?, @RequestBody args: UpdateMenuArgs): Result<Menu> {
    id.requireNonnull { "菜单ID为空" }
    val menuDo = menuService.update(id, args)
    val menu = menuDo.toMenu()
    return Result.success(menu)
  }

  /**
   * 变更父菜单
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/menu/change_parent?id=393403253447983104&parentId=393403096727814144
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": {
   *       "id": "393403253447983104",
   *       "parentId": "393403096727814144",
   *       "platform": "iam",
   *       "terminal": "iam_web",
   *       "name": "告警管理",
   *       "type": "MENU",
   *       "order": 0,
   *       "icon": "",
   *       "selectedIcon": "",
   *       "url": "",
   *       "path": "",
   *       "apis": [
   *         ""
   *       ],
   *       "createdTime": "2022-08-23 23:50:42.590",
   *       "updatedTime": "2022-08-24 00:08:24.668"
   *     }
   *   }
   * </pre>
   *
   * @param id       菜单id
   * @param parentId 父菜单id
   */
  @Operation("变更父菜单")
  @PostMapping("/change_parent")
  suspend fun changeParent(id: Long?, parentId: Long?): Result<Menu> {
    id.requireNonnull { "菜单ID不能为空" }
    val menuDo = menuService.changeParent(id, parentId)
    val menu = menuDo.toMenu()
    return Result.success(menu)
  }

  /**
   * 删除菜单
   * <pre>
   *   <p><b>请求示例</b></p>
   *   POST {{base_url}}/iam/menu/delete_menu?id=393403444112654336
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success"
   *   }
   * </pre>
   *
   * @param id 菜单id
   */
  @Operation("删除菜单")
  @PostMapping("/delete_menu")
  suspend fun delete(id: Long?): Result<Void> {
    id.requireNonnull { "菜单id为空" }
    menuService.delete(id)
    return Result.success()
  }

  /**
   * 获取终端下的菜单树
   * <pre>
   *   <p><b>请求示例</b></p>
   *   GET {{base_url}}/iam/menu/menu_tree_by_terminal?terminal=iam_web
   *   Authorization: {{token_type}} {{access_token}}
   *   <p><b>响应示例</b></p>
   *   {
   *     "success": true,
   *     "message": "success",
   *     "data": [
   *       {
   *         "id": "393403096727814144",
   *         "parentId": "-1",
   *         "platform": "iam",
   *         "terminal": "iam_web",
   *         "name": "资源",
   *         "type": "MENU",
   *         "order": 0,
   *         "icon": "",
   *         "selectedIcon": "",
   *         "url": "",
   *         "path": "",
   *         "apis": [],
   *         "createdTime": "2022-08-23 23:50:05.226",
   *         "updatedTime": "2022-08-23 23:54:14.410",
   *         "child": [
   *           {
   *             "id": "393403182648131584",
   *             "parentId": "393403096727814144",
   *             "platform": "iam",
   *             "terminal": "iam_web",
   *             "name": "资源目录",
   *             "type": "MENU",
   *             "order": 0,
   *             "icon": "",
   *             "selectedIcon": "",
   *             "url": "",
   *             "path": "",
   *             "apis": [
   *               ""
   *             ],
   *             "createdTime": "2022-08-23 23:50:25.710",
   *             "updatedTime": "2022-08-23 23:50:25.710",
   *             "child": []
   *           },
   *           {
   *             "id": "393403229808885760",
   *             "parentId": "393403096727814144",
   *             "platform": "iam",
   *             "terminal": "iam_web",
   *             "name": "资源监控",
   *             "type": "MENU",
   *             "order": 0,
   *             "icon": "",
   *             "selectedIcon": "",
   *             "url": "",
   *             "path": "",
   *             "apis": [
   *               ""
   *             ],
   *             "createdTime": "2022-08-23 23:50:36.954",
   *             "updatedTime": "2022-08-23 23:50:36.954",
   *             "child": []
   *           },
   *           {
   *             "id": "393403253447983104",
   *             "parentId": "393403096727814144",
   *             "platform": "iam",
   *             "terminal": "iam_web",
   *             "name": "告警管理",
   *             "type": "MENU",
   *             "order": 0,
   *             "icon": "",
   *             "selectedIcon": "",
   *             "url": "",
   *             "path": "",
   *             "apis": [
   *               ""
   *             ],
   *             "createdTime": "2022-08-23 23:50:42.590",
   *             "updatedTime": "2022-08-24 00:08:24.668",
   *             "child": [
   *               {
   *                 "id": "393403318124150784",
   *                 "parentId": "393403253447983104",
   *                 "platform": "iam",
   *                 "terminal": "iam_web",
   *                 "name": "告警规则",
   *                 "type": "MENU",
   *                 "order": 0,
   *                 "icon": "",
   *                 "selectedIcon": "",
   *                 "url": "",
   *                 "path": "",
   *                 "apis": [
   *                   ""
   *                 ],
   *                 "createdTime": "2022-08-23 23:50:58.010",
   *                 "updatedTime": "2022-08-24 00:08:24.671",
   *                 "child": []
   *               },
   *               {
   *                 "id": "393403367289782272",
   *                 "parentId": "393403253447983104",
   *                 "platform": "iam",
   *                 "terminal": "iam_web",
   *                 "name": "活跃告警",
   *                 "type": "MENU",
   *                 "order": 0,
   *                 "icon": "",
   *                 "selectedIcon": "",
   *                 "url": "",
   *                 "path": "",
   *                 "apis": [
   *                   ""
   *                 ],
   *                 "createdTime": "2022-08-23 23:51:09.732",
   *                 "updatedTime": "2022-08-24 00:08:24.671",
   *                 "child": []
   *               },
   *               {
   *                 "id": "393403388169027584",
   *                 "parentId": "393403253447983104",
   *                 "platform": "iam",
   *                 "terminal": "iam_web",
   *                 "name": "历史告警",
   *                 "type": "MENU",
   *                 "order": 0,
   *                 "icon": "",
   *                 "selectedIcon": "",
   *                 "url": "",
   *                 "path": "",
   *                 "apis": [
   *                   ""
   *                 ],
   *                 "createdTime": "2022-08-23 23:51:14.710",
   *                 "updatedTime": "2022-08-24 00:08:24.672",
   *                 "child": []
   *               },
   *               {
   *                 "id": "393403444112654336",
   *                 "parentId": "393403253447983104",
   *                 "platform": "iam",
   *                 "terminal": "iam_web",
   *                 "name": "屏蔽规则",
   *                 "type": "MENU",
   *                 "order": 0,
   *                 "icon": "",
   *                 "selectedIcon": "",
   *                 "url": "",
   *                 "path": "",
   *                 "apis": [
   *                   ""
   *                 ],
   *                 "createdTime": "2022-08-23 23:51:28.048",
   *                 "updatedTime": "2022-08-24 00:08:24.672",
   *                 "child": []
   *               }
   *             ]
   *           }
   *         ]
   *       }
   *     ]
   *   }
   * </pre>
   *
   * @param terminal 终端编码
   */
  @GetMapping("/menu_tree_by_terminal")
  suspend fun getMenuTreeByTerminal(terminal: String?): ListResult<MenuTree> {
    terminal.requireNotBlank { "终端编码为空" }
    val menuDos = menuRepository.findAllByTerminal(terminal)
    val treeList = menuDos.map { MenuTree.of(it) }.toTreeList()
    return ListResult.of(treeList)
  }
}
