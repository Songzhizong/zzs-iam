package com.zzs.iam.server.domain.model.user;

import com.zzs.iam.common.constants.UserAuthStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2022/8/26
 */
@Document("iam_user_config")
public class UserConfigDo {

  @Id
  private long id;

  /** 用户ID */
  @Indexed
  private long userId;

  /** mfa设备码 */
  @Nonnull
  private String mfaCode = "";

  /** 可用的验证策略 */
  @Nonnull
  private Set<UserAuthStrategy> authStrategies = new HashSet<>();

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  /** 移除验证策略 */
  public void removeStrategy(@Nonnull UserAuthStrategy strategy) {
    authStrategies.remove(strategy);
  }

  /** 新增验证策略 */
  public void addStrategy(@Nonnull UserAuthStrategy strategy) {
    authStrategies.add(strategy);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  @Nonnull
  public String getMfaCode() {
    return mfaCode;
  }

  public void setMfaCode(@Nonnull String mfaCode) {
    this.mfaCode = mfaCode;
  }

  @Nonnull
  public Set<UserAuthStrategy> getAuthStrategies() {
    return authStrategies;
  }

  public void setAuthStrategies(@Nonnull Set<UserAuthStrategy> authStrategies) {
    this.authStrategies = authStrategies;
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
