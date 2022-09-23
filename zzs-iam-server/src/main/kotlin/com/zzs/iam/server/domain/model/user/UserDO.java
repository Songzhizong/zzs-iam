package com.zzs.iam.server.domain.model.user;

import com.zzs.framework.core.crypto.AES;
import com.zzs.framework.core.event.EventSuppliers;
import com.zzs.framework.core.event.EventTuple;
import com.zzs.framework.core.exception.BadRequestException;
import com.zzs.framework.core.lang.StringUtils;
import com.zzs.iam.common.event.user.*;
import com.zzs.iam.common.pojo.User;
import com.zzs.iam.server.domain.model.user.event.builder.UserCreatedBuilder;
import com.zzs.iam.server.dto.args.UpdateUserArgs;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author 宋志宗 on 2022/8/15
 */
@Document("iam_user")
public class UserDO {
  private static final Logger log = LoggerFactory.getLogger(UserDO.class);
  private static final String SEC = "ifGj9v66ptj7p8sMpi2McNNmf4V3D97D";

  @Id
  private long id;

  /** 姓名 */
  @Nonnull
  private String name = "";

  /** 昵称 */
  @Nonnull
  private String nickname = "";

  /** 账号 */
  @Nonnull
  @Indexed(unique = true)
  private String account = "";

  /** 手机号码 */
  @Nonnull
  @Indexed(unique = true)
  private String phone = "";

  /** 邮箱 */
  @Nonnull
  @Indexed(unique = true)
  private String email = "";

  /** 密码 */
  @Nonnull
  private String password = "";

  /** 密码修改日期 */
  @Nullable
  private LocalDate passwordDate = LocalDate.MIN;

  /** 头像 */
  @Nonnull
  private String profilePhoto = "";


  /** 是否已被冻结 */
  private boolean frozen = false;

  @Version
  private long version;

  @CreatedDate
  private LocalDateTime createdTime;

  @LastModifiedDate
  private LocalDateTime updatedTime;

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

  /** 更新密码 并刷新密码修改时间 */
  @Nonnull
  public EventSuppliers setupPassword(@Nonnull String password) {
    setPassword(password);
    setPasswordDate(LocalDate.now());
    return EventSuppliers.of(new UserPasswordChanged(getId()));
  }

  /** 重置密码 (用户登录后应该立即修改密码) */
  @Nonnull
  public EventSuppliers resetPassword(@Nonnull String password) {
    setPassword(password);
    setPasswordDate(LocalDate.MIN);
    return EventSuppliers.of(new UserPasswordChanged(getId()));
  }

  private static boolean isEmptyUnique(@NotNull String encrypted) {
    return StringUtils.startsWith(encrypted, "$empty$");
  }

  /** 冻结 */
  @Nonnull
  public EventSuppliers freeze() {
    if (this.isFrozen()) {
      return EventSuppliers.create();
    }
    setFrozen(true);
    return EventSuppliers.of(new UserFrozen(this.getId()));
  }

  /** 解冻 */
  public EventSuppliers unfreeze() {
    if (!this.isFrozen()) {
      return EventSuppliers.create();
    }
    setFrozen(false);
    return EventSuppliers.create();
  }


  @Nonnull
  public static EventTuple<UserDO> create(@Nullable String name,
                                          @Nullable String nickname,
                                          @Nullable String account,
                                          @Nullable String phone,
                                          @Nullable String email) {
    if (StringUtils.isAllBlank(account, phone, email)) {
      log.info("创建用户失败, 账号、手机号、邮箱 不能同时为空");
      throw new BadRequestException("账号、手机号、邮箱 不能同时为空");
    }
    if (StringUtils.isNotBlank(account)
      && StringUtils.isNotBlank(email)
      && account.equals(email)) {
      throw new BadRequestException("账号与邮箱不能相同");
    }
    UserDO userDo = new UserDO();
    userDo.setName(name);
    userDo.setNickname(nickname);
    userDo.setAccount(account);
    userDo.setPhone(phone);
    userDo.setEmail(email);
    return EventTuple.of(userDo, new UserCreatedBuilder(userDo));
  }

  /** 修改账号 */
  @Nonnull
  public EventSuppliers changeAccount(@Nullable String account) {
    if (getAccount().equals(account)) {
      return EventSuppliers.create();
    }
    this.setAccount(account);
    return EventSuppliers.of(new UserAccountChanged(getId(), getAccount()));
  }

  /** 修改手机号 */
  @Nonnull
  public EventSuppliers changePhone(@Nullable String phone) {
    this.setPhone(phone);
    return EventSuppliers.of(new UserPhoneChanged(getId(), getPhone()));
  }

  /** 修改邮箱 */
  @Nonnull
  public EventSuppliers changeEmail(@Nullable String email) {
    this.setEmail(email);
    return EventSuppliers.of(new UserEmailChanged(getId(), getEmail()));
  }

  /** 选择性更新用户信息 */
  @Nonnull
  public EventSuppliers selectivityUpdate(@Nonnull UpdateUserArgs args) {
    String name = args.getName();
    String nickname = args.getNickname();
    String account = args.getAccount();
    String phone = args.getPhone();
    String email = args.getEmail();
    String profilePhoto = args.getProfilePhoto();

    boolean updated = false;
    if (name != null) {
      setName(name);
      updated = true;
    }
    if (nickname != null) {
      setNickname(nickname);
      updated = true;
    }
    if (account != null) {
      setAccount(account);
      updated = true;
    }
    if (phone != null) {
      setPhone(phone);
      updated = true;
    }
    if (email != null) {
      setEmail(email);
      updated = true;
    }
    if (profilePhoto != null) {
      setProfilePhoto(profilePhoto);
      updated = true;
    }
    if (!updated) {
      return EventSuppliers.create();
    }
    if (StringUtils.isAllBlank(getAccount(), getPhone(), getEmail())) {
      log.info("创建用户失败, 账号、手机号、邮箱 不能同时为空");
      throw new BadRequestException("账号、手机号、邮箱 不能同时为空");
    }
    return EventSuppliers.of(new UserUpdated(this.toUser()));
  }

  @Nonnull
  public User toUser() {
    User user = new User();
    user.setId(getId());
    user.setName(getName());
    user.setNickname(getNickname());
    user.setAccount(getAccount());
    user.setPhone(getPhone());
    user.setEmail(getEmail());
    user.setProfilePhoto(getProfilePhoto());
    user.setFrozen(isFrozen());
    user.setPasswordDate(getPasswordDate());
    user.setCreatedTime(getCreatedTime());
    user.setUpdatedTime(getUpdatedTime());
    return user;
  }

  // ------------------------------ getter setter ~ ~ ~

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  @Nonnull
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@Nullable String nickname) {
    if (StringUtils.isBlank(nickname)) {
      nickname = "";
    }
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
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nonnull String password) {
    this.password = password;
  }

  @Nullable
  public LocalDate getPasswordDate() {
    return passwordDate;
  }

  public void setPasswordDate(@Nullable LocalDate passwordDate) {
    this.passwordDate = passwordDate;
  }

  @Nonnull
  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(@Nullable String profilePhoto) {
    if (StringUtils.isBlank(profilePhoto)) {
      profilePhoto = "";
    }
    this.profilePhoto = profilePhoto;
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
