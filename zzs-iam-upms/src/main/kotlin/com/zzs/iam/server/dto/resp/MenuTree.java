package com.zzs.iam.server.dto.resp;

import cn.idealframework2.lang.TreeNode;
import com.zzs.iam.common.pojo.Menu;
import com.zzs.iam.server.domain.model.web.MenuDO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class MenuTree extends Menu implements TreeNode {

  @Nonnull
  private List<MenuTree> child = new ArrayList<>();

  @SuppressWarnings("DuplicatedCode")
  @Nonnull
  public static MenuTree of(@Nonnull MenuDO menuDo) {
    MenuTree menuTree = new MenuTree();
    menuTree.setId(menuDo.getId());
    menuTree.setParentId(menuDo.getParentId());
    menuTree.setPlatform(menuDo.getPlatform());
    menuTree.setTerminal(menuDo.getTerminal());
    menuTree.setName(menuDo.getName());
    menuTree.setType(menuDo.getType());
    menuTree.setOrder(menuDo.getOrder());
    menuTree.setIcon(menuDo.getIcon());
    menuTree.setSelectedIcon(menuDo.getSelectedIcon());
    menuTree.setUrl(menuDo.getUrl());
    menuTree.setPath(menuDo.getPath());
    menuTree.setApis(menuDo.getApis());
    menuTree.setCreatedTime(menuDo.getCreatedTime());
    menuTree.setUpdatedTime(menuDo.getUpdatedTime());
    return menuTree;
  }

  @Nullable
  @Override
  public Object getParentNodeId() {
    return getParentId();
  }

  @Nonnull
  @Override
  public Object getNodeId() {
    return getId();
  }

  @Nonnull
  @Override
  public List<? extends TreeNode> getChildNodes() {
    return child;
  }

  @Nonnull
  public List<MenuTree> getChild() {
    return child;
  }

  public void setChild(@Nonnull List<MenuTree> child) {
    this.child = child;
  }
}
