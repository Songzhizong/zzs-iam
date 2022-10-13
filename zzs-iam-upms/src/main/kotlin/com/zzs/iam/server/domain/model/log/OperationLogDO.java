package com.zzs.iam.server.domain.model.log;

import cn.idealframework2.trace.OperationLog;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;

/**
 * 操作日志
 *
 * @author 宋志宗 on 2022/9/23
 */
@Document("iam_operation_log")
public class OperationLogDO {

  /** 主键 */
  @Id
  private long id;

  /** 平台编码 */
  @Nonnull
  @Indexed(background = true)
  private String platform = "";

  /** 租户ID */
  @Nonnull
  @Indexed(background = true)
  private String tenantId = "";

  /** 用户ID */
  @Nonnull
  @Indexed(background = true)
  private String userId = "";

  @Nonnull
  private String traceId = "";

  /** 系统名称 */
  @Nonnull
  private String system = "";

  /** 操作名称 */
  @Nonnull
  private String name = "";

  /** 操作详情 */
  @Nonnull
  private String details = "";

  /** 请求路径 */
  @Nonnull
  private String path = "";

  /** 原始请求地址 */
  @Nonnull
  private String originalIp = "";

  /** 浏览器UA */
  @Nonnull
  private String userAgent = "";

  /** 是否成功 */
  private boolean success = true;

  /** 执行信息, 可用于记录错误信息 */
  @Nonnull
  private String message = "";

  /** 变更前信息 */
  @Nullable
  private String before;

  /** 变更后信息 */
  @Nullable
  private String after;

  /** 耗时, 单位毫秒 */
  private int consuming = -1;

  /** 操作时间 */
  @Indexed(expireAfterSeconds = 15552000, background = true)
  private long operationTime = -1L;

  @Nonnull
  public static OperationLogDO create(@Nonnull OperationLog operationLog) {
    OperationLogDO operationLogDo = new OperationLogDO();
    operationLogDo.setPlatform(operationLog.getPlatform());
    operationLogDo.setTenantId(operationLog.getTenantId());
    operationLogDo.setUserId(operationLog.getUserId());
    operationLogDo.setTraceId(operationLog.getTraceId());
    operationLogDo.setSystem(operationLog.getSystem());
    operationLogDo.setName(operationLog.getName());
    operationLogDo.setDetails(operationLog.getDetails());
    operationLogDo.setPath(operationLog.getPath());
    operationLogDo.setOriginalIp(operationLog.getOriginalIp());
    operationLogDo.setUserAgent(operationLog.getUserAgent());
    operationLogDo.setSuccess(operationLog.isSuccess());
    operationLogDo.setMessage(operationLog.getMessage());
    operationLogDo.setBefore(operationLog.getBefore());
    operationLogDo.setAfter(operationLog.getAfter());
    operationLogDo.setConsuming(operationLog.getConsuming());
    operationLogDo.setOperationTime(operationLog.getOperationTime());
    return operationLogDo;
  }

  public static void main(String[] args) {
    System.out.println(Duration.ofDays(180).getSeconds());
  }

  public long getId() {
    return id;
  }

  public OperationLogDO setId(long id) {
    this.id = id;
    return this;
  }

  @Nonnull
  public String getPlatform() {
    return platform;
  }

  public OperationLogDO setPlatform(@Nonnull String platform) {
    this.platform = platform;
    return this;
  }

  @Nonnull
  public String getTenantId() {
    return tenantId;
  }

  public OperationLogDO setTenantId(@Nonnull String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  public OperationLogDO setUserId(@Nonnull String userId) {
    this.userId = userId;
    return this;
  }

  @Nonnull
  public String getTraceId() {
    return traceId;
  }

  public OperationLogDO setTraceId(@Nonnull String traceId) {
    this.traceId = traceId;
    return this;
  }

  @Nonnull
  public String getSystem() {
    return system;
  }

  public OperationLogDO setSystem(@Nonnull String system) {
    this.system = system;
    return this;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public OperationLogDO setName(@Nonnull String name) {
    this.name = name;
    return this;
  }

  @Nonnull
  public String getDetails() {
    return details;
  }

  public OperationLogDO setDetails(@Nonnull String details) {
    this.details = details;
    return this;
  }

  @Nonnull
  public String getPath() {
    return path;
  }

  public OperationLogDO setPath(@Nonnull String path) {
    this.path = path;
    return this;
  }

  @Nonnull
  public String getOriginalIp() {
    return originalIp;
  }

  public OperationLogDO setOriginalIp(@Nonnull String originalIp) {
    this.originalIp = originalIp;
    return this;
  }

  @Nonnull
  public String getUserAgent() {
    return userAgent;
  }

  public OperationLogDO setUserAgent(@Nonnull String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

  public boolean isSuccess() {
    return success;
  }

  public OperationLogDO setSuccess(boolean success) {
    this.success = success;
    return this;
  }

  @Nonnull
  public String getMessage() {
    return message;
  }

  public OperationLogDO setMessage(@Nonnull String message) {
    this.message = message;
    return this;
  }

  @Nullable
  public String getBefore() {
    return before;
  }

  public OperationLogDO setBefore(@Nullable String before) {
    this.before = before;
    return this;
  }

  @Nullable
  public String getAfter() {
    return after;
  }

  public OperationLogDO setAfter(@Nullable String after) {
    this.after = after;
    return this;
  }

  public int getConsuming() {
    return consuming;
  }

  public OperationLogDO setConsuming(int consuming) {
    this.consuming = consuming;
    return this;
  }

  public long getOperationTime() {
    return operationTime;
  }

  public OperationLogDO setOperationTime(long operationTime) {
    this.operationTime = operationTime;
    return this;
  }
}
