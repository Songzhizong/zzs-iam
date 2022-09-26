package com.zzs.iam.server.domain.model.org;

import com.zzs.framework.core.crypto.AES;
import com.zzs.framework.core.event.EventSuppliers;
import com.zzs.framework.core.lang.Sets;
import com.zzs.framework.core.lang.StringUtils;
import com.zzs.iam.common.event.user.PlatformUserFrozen;
import com.zzs.iam.common.pojo.JoinedUser;
import com.zzs.iam.common.pojo.User;
import com.zzs.iam.server.domain.model.user.AuthUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * 平台用户信息
 *
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_platform_user")
@CompoundIndexes({
  @CompoundIndex(name = "uk_platform_user", def = "{platform:1,user_id:1}", unique = true),
})
public class PlatformUserDO {
  private static final String SEC = "!ENwKJ.76x3iZMH6vCaQti6Zd8zkC2Jr";

  @Id
  private long id;

  /** 平台编码 */
  @Nonnull
  private String platform = "";

  /** 用户id */
  @Nonnull
  @Indexed
  private String userId = "";

  /** 姓名 */
  @Nonnull
  private String name = "";

  /** 昵称 */
  private String nickname = "";

  /** 账号 */
  @Nonnull
  @Indexed
  private String account = "";

  /** 手机号 */
  @Nonnull
  @Indexed
  private String phone = "";

  /** 邮箱 */
  @Nonnull
  @Indexed
  private String email = "";

  /** 角色列表 */
  @Nonnull
  @Indexed
  private Set<Long> roles = Collections.emptySet();

  /** 是否已被冻结 */
  private boolean frozen = false;

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

  @Nonnull
  public EventSuppliers freeze() {
    if (isFrozen()) {
      return EventSuppliers.create();
    }
    setFrozen(true);
    return EventSuppliers.of(new PlatformUserFrozen(this.getPlatform(), this.toJoinedUser()));
  }

  @Nonnull
  public EventSuppliers unfreeze() {
    if (!isFrozen()) {
      return EventSuppliers.create();
    }
    setFrozen(false);
    return EventSuppliers.create();
//    return EventSuppliers.of(new PlatformUserUnfreezed(this.toPlatformUser()));
  }

  @Nonnull
  public EventSuppliers changeRoles(@Nonnull Set<Long> roles) {
    if (Sets.isEmpty(roles) && Sets.isEmpty(getRoles())) {
      return EventSuppliers.create();
    }
    if (getRoles().containsAll(roles)) {
      return EventSuppliers.create();
    }
    setRoles(roles);
    return EventSuppliers.create();
  }

  public void update(@Nonnull User user) {
    setName(user.getName());
    setNickname(user.getNickname());
    setAccount(user.getAccount());
    setPhone(user.getPhone());
    setEmail(user.getEmail());
  }

  @SuppressWarnings("DuplicatedCode")
  @Nonnull
  public static PlatformUserDO create(@Nonnull AuthUser user,
                                      @Nonnull String platform) {
    PlatformUserDO platformUserDo = new PlatformUserDO();
    platformUserDo.setPlatform(platform);
    platformUserDo.setUserId(user.getUserId());
    platformUserDo.setName(user.getName());
    platformUserDo.setNickname(user.getNickname());
    platformUserDo.setAccount(user.getAccount());
    platformUserDo.setPhone(user.getPhone());
    platformUserDo.setEmail(user.getEmail());
    return platformUserDo;
  }

  @Nonnull
  public static String encrypt(@Nullable String text) {
    if (StringUtils.isBlank(text)) {
      return randomEmptyUnique();
    }
    return AES.encrypt(text, SEC);
  }

  @Nonnull
  public static String decrypt(@Nonnull String encrypted) {
    if (isEmptyUnique(encrypted)) {
      return "";
    }
    return AES.decrypt(encrypted, SEC);
  }

  @NotNull
  private static String randomEmptyUnique() {
    return "$empty$" + UUID.randomUUID().toString().replace("-", "");
  }

  private static boolean isEmptyUnique(@NotNull String encrypted) {
    return StringUtils.startsWith(encrypted, "$empty$");
  }

  @Nonnull
  @SuppressWarnings("DuplicatedCode")
  public JoinedUser toJoinedUser() {
    JoinedUser user = new JoinedUser();
    user.setUserId(getUserId());
    user.setName(getName());
    user.setNickname(getNickname());
    user.setAccount(getAccount());
    user.setPhone(getPhone());
    user.setEmail(getEmail());
    user.setRoles(getRoles());
    user.setFrozen(isFrozen());
    user.setCreatedTime(getCreatedTime());
    user.setUpdatedTime(getUpdatedTime());
    return user;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(@Nonnull String platform) {
    this.platform = platform;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public void setUserId(@Nonnull String userId) {
    this.userId = userId;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    if (StringUtils.isBlank(name)) {
      name = "";
    }
    this.name = name;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  @Nonnull
  public String getAccount() {
    if (isEmptyUnique(account)) {
      return "";
    }
    return account;
  }

  public void setAccount(@Nullable String account) {
    if (StringUtils.isBlank(account)) {
      account = randomEmptyUnique();
    }
    this.account = account;
  }

  @Nonnull
  public String getPhone() {
    return decrypt(phone);
  }

  public void setPhone(@Nullable String phone) {
    this.phone = encrypt(phone);
  }

  @Nonnull
  public String getEmail() {
    return decrypt(email);
  }

  public void setEmail(@Nullable String email) {
    this.email = encrypt(email);
  }

  @Nonnull
  public Set<Long> getRoles() {
    return roles;
  }

  public void setRoles(@Nonnull Set<Long> roles) {
    this.roles = roles;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
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
