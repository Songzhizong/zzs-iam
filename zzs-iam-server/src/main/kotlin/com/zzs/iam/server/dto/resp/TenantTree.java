package com.zzs.iam.server.dto.resp;

import cn.idealframework2.lang.TreeNode;
import com.zzs.iam.common.pojo.Tenant;
import com.zzs.iam.server.domain.model.org.TenantDO;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋志宗 on 2022/8/19
 */
public class TenantTree extends Tenant implements TreeNode {

  private List<TenantTree> child = new ArrayList<>();

  @Nonnull
  public static TenantTree of(@Nonnull TenantDO tenantDo) {
    TenantTree tenantTree = new TenantTree();
    tenantTree.setId(tenantDo.getId());
    tenantTree.setParentId(tenantDo.getParentId());
    tenantTree.setPlatform(tenantDo.getPlatform());
    tenantTree.setName(tenantDo.getName());
    tenantTree.setAddress(tenantDo.getAddress());
    tenantTree.setNote(tenantDo.getNote());
    tenantTree.setFrozen(tenantDo.isFrozen());
    tenantTree.setCreatedTime(tenantDo.getCreatedTime());
    tenantTree.setUpdatedTime(tenantDo.getUpdatedTime());
    return tenantTree;
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

  public List<TenantTree> getChild() {
    return child;
  }

  public void setChild(List<TenantTree> child) {
    this.child = child;
  }
}
