package com.zzs.iam.upms.domain.model.user;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * 用户第三方平台授权信息
 *
 * @author 宋志宗 on 2022/8/23
 */
@Document("iam_user_other_plat_auth")
@CompoundIndexes({
  @CompoundIndex(name = "uk_code_uid", def = "{plat_code:1,other_plat_user_id:1}", unique = true),
  @CompoundIndex(name = "uk_user_code", def = "{user_id:1,plat_code:1}", unique = true),
})
public class OtherPlatAuthDO {

  @Id
  private long id;

  /** 用户ID */
  private long userId;

  /** 第三方平台编码 */
  @Nonnull
  private String platCode = "";

  /** 第三方平台用户ID */
  @Nonnull
  private String otherPlatUserId = "";


  @CreatedDate
  private LocalDateTime createdTime;

  @Nonnull
  public static OtherPlatAuthDO create(long userId,
                                       @Nonnull String platCode,
                                       @Nonnull String otherPlatUserId) {
    OtherPlatAuthDO otherPlatAuthDo = new OtherPlatAuthDO();
    otherPlatAuthDo.setUserId(userId);
    otherPlatAuthDo.setPlatCode(platCode);
    otherPlatAuthDo.setOtherPlatUserId(otherPlatUserId);
    return otherPlatAuthDo;
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
  public String getPlatCode() {
    return platCode;
  }

  public void setPlatCode(@Nonnull String platCode) {
    this.platCode = platCode;
  }

  @Nonnull
  public String getOtherPlatUserId() {
    return otherPlatUserId;
  }

  public void setOtherPlatUserId(@Nonnull String otherPlatUserId) {
    this.otherPlatUserId = otherPlatUserId;
  }

  public LocalDateTime getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDateTime createdTime) {
    this.createdTime = createdTime;
  }
}
