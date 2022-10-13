package com.zzs.iam.upms.domain.model.web;

import cn.idealframework2.lang.StringUtils;
import cn.idealframework2.utils.Asserts;
import com.zzs.iam.common.constants.MenuType;
import com.zzs.iam.common.pojo.Menu;
import com.zzs.iam.upms.dto.args.UpdateMenuArgs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_menu")
public class MenuDO {
  public static final String ROUTER_SEPARATOR = "-";

  @Id
  private long id;

  /** 父菜单id */
  @Indexed
  private long parentId = -1;

  @Nonnull
  @Indexed
  private String parentRouter = "";

  /** 平台编码 */
  @Nonnull
  @Indexed
  private String platform = "";

  /** 终端编码 */
  @Nonnull
  private String terminal = "";

  /** 菜单名称 */
  @Nonnull
  private String name = "";

  /** 菜单类型 */
  @Nonnull
  private MenuType type = MenuType.MENU;

  /** 排序 */
  private int order = 0;

  /** 调用api列表 */
  @Nonnull
  private Set<String> apis = Collections.emptySet();

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public static MenuDO create(@Nullable MenuDO parent,
                              @Nonnull TerminalDO terminal,
                              @Nonnull String name,
                              @Nonnull MenuType type,
                              int order,
                              @Nullable Set<String> apis) {
    MenuDO menuDo = new MenuDO();
    if (parent != null) {
      menuDo.setParentId(parent.getId());
      menuDo.setParentRouter(parent.generateRouter());
    }
    menuDo.setPlatform(terminal.getPlatform());
    menuDo.setTerminal(terminal.getCode());
    menuDo.setName(name);
    menuDo.setType(type);
    menuDo.setOrder(order);
    menuDo.setApis(apis);
    return menuDo;
  }


  public void updated(@Nonnull UpdateMenuArgs args) {
    String name = args.getName();
    Asserts.notBlank(name, "菜单名称不能为空");
    MenuType type = args.getType();
    Asserts.nonnull(type, "菜单类型不能为空");
    int order = args.getOrder();
    Set<String> apis = null;
    if (args.getApis() != null) {
      apis = new HashSet<>(args.getApis());
    }
    setName(name);
    setType(type);
    setOrder(order);
    setApis(apis);
  }

  public void changeParent(@Nullable MenuDO parent) {
    if (parent == null) {
      setParentId(-1);
      setParentRouter("");
      return;
    }
    setParentId(parent.getId());
    setParentRouter(parent.generateRouter());
  }

  @Nonnull
  public Menu toMenu() {
    Menu menu = new Menu();
    menu.setId(getId());
    menu.setParentId(getParentId());
    menu.setPlatform(getPlatform());
    menu.setTerminal(getTerminal());
    menu.setName(getName());
    menu.setApis(getApis());
    menu.setCreatedTime(getCreatedTime());
    menu.setUpdatedTime(getUpdatedTime());
    return menu;
  }

  @Nonnull
  public String generateRouter() {
    String parentRouter = this.getParentRouter();
    long id = this.getId();
    String idStr = String.valueOf(id);
    if (StringUtils.isBlank(parentRouter)) {
      return idStr + ROUTER_SEPARATOR;
    } else {
      return parentRouter + idStr + ROUTER_SEPARATOR;
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  @Nonnull
  public String getParentRouter() {
    return parentRouter;
  }

  public void setParentRouter(@Nonnull String parentRouter) {
    this.parentRouter = parentRouter;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  @Nonnull
  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(@Nonnull String terminal) {
    this.terminal = terminal;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  @Nonnull
  public MenuType getType() {
    return type;
  }

  public void setType(@Nonnull MenuType type) {
    this.type = type;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Nonnull
  public Set<String> getApis() {
    return apis;
  }

  public void setApis(@Nullable Set<String> apis) {
    if (apis == null) {
      apis = Collections.emptySet();
    }
    this.apis = apis;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }

  public LocalDateTime getUpdatedTime() {
    return updatedTime;
  }

  public void setUpdatedTime(LocalDateTime updatedTime) {
    this.updatedTime = updatedTime;
  }
}
