package com.zzs.iam.upms.domain.model.log;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * 用户登录日志
 *
 * @author 宋志宗 on 2022/9/24
 */
@Document("iam_login_log")
public class LoginLogDO {

  /** 主键 */
  @Id
  private long id;

  /** 用户ID */
  @Indexed(background = true)
  private String userId;

  /** 登录平台 */
  @Nonnull
  @Indexed(background = true)
  private String platform = "";

  /** 授权客户端ID */
  @Nonnull
  private String clientId = "";

  /** 登录IP地址 */
  @Nonnull
  private String ip = "";

  /** 登录位置 */
  @Nonnull
  private String location = "";

  /** 登录时间 */
  @CreatedDate
  private LocalDateTime loginTime;

  @Nonnull
  public static LoginLogDO create(@Nonnull String userId,
                                  @Nonnull String platform,
                                  @Nonnull String clientId,
                                  @Nonnull String ip) {
    LoginLogDO loginLogDO = new LoginLogDO();
    loginLogDO.setUserId(userId);
    loginLogDO.setPlatform(platform);
    loginLogDO.setClientId(clientId);
    loginLogDO.setIp(ip);
//    loginLogDO.setLocation();
    return loginLogDO;
  }

  public long getId() {
    return id;
  }

  public LoginLogDO setId(long id) {
    this.id = id;
    return this;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public LoginLogDO setUserId(@Nonnull String userId) {
    this.userId = userId;
    return this;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public LoginLogDO setPlatform(@Nonnull String platform) {
    this.platform = platform;
    return this;
  }

  @Nonnull
  public String getClientId() {
    return clientId;
  }

  public LoginLogDO setClientId(@Nonnull String clientId) {
    this.clientId = clientId;
    return this;
  }

  @Nonnull
  public String getIp() {
    return ip;
  }

  public LoginLogDO setIp(@Nonnull String ip) {
    this.ip = ip;
    return this;
  }

  @Nonnull
  public String getLocation() {
    return location;
  }

  public LoginLogDO setLocation(@Nonnull String location) {
    this.location = location;
    return this;
  }

  public LocalDateTime getLoginTime() {
    return loginTime;
  }

  public LoginLogDO setLoginTime(LocalDateTime loginTime) {
    this.loginTime = loginTime;
    return this;
  }
}
