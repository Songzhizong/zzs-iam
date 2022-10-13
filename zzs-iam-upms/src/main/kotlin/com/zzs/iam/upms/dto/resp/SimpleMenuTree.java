package com.zzs.iam.upms.dto.resp;

import cn.idealframework2.lang.TreeNode;
import com.zzs.iam.common.pojo.SimpleMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/18
 */
public class SimpleMenuTree extends SimpleMenu implements TreeNode {

  @Nonnull
  private List<SimpleMenuTree> child = new ArrayList<>();

  @SuppressWarnings("DuplicatedCode")
  @Nonnull
  public static SimpleMenuTree of(@Nonnull SimpleMenu simpleMenu) {
    SimpleMenuTree simpleMenuTree = new SimpleMenuTree();
    simpleMenuTree.setId(simpleMenu.getId());
    simpleMenuTree.setParentId(simpleMenu.getParentId());
    simpleMenuTree.setTerminal(simpleMenu.getTerminal());
    simpleMenuTree.setName(simpleMenu.getName());
    simpleMenuTree.setType(simpleMenu.getType());
    simpleMenuTree.setOrder(simpleMenu.getOrder());
    return simpleMenuTree;

  }

  @Nonnull
  public List<SimpleMenuTree> getChild() {
    return child;
  }

  public void setChild(@Nonnull List<SimpleMenuTree> child) {
    this.child = child;
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
    return getChild();
  }
}
