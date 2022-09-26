package com.zzs.iam.server.domain.model.user;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * 用户历史密码
 *
 * @author 宋志宗 on 2022/8/23
 */
@Document("iam_user_hist_password")
public class HistPasswordDO {

  @Id
  private long id;

  /** 用户ID */
  @Indexed
  private long userId;

  /** 密码 */
  @Nonnull
  private String password = "";

  @CreatedDate
  private LocalDateTime createdTime;

  @Nonnull
  public static HistPasswordDO create(long userId, @Nonnull String password) {
    HistPasswordDO histPasswordDo = new HistPasswordDO();
    histPasswordDo.setUserId(userId);
    histPasswordDo.setPassword(password);
    return histPasswordDo;
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
  public String getPassword() {
    return password;
  }

  public void setPassword(@Nonnull String password) {
    this.password = password;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }
}
