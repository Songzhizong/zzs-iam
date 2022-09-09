package com.zzs.iam.common.constants;

import javax.annotation.Nonnull;

/**
 * @author 宋志宗 on 2022/8/16
 */
public interface IamHeaders {

  /** 平台编码 */
  @Nonnull
  String PLATFORM = "X-Platform";

  /** 是否多租户平台 */
  @Nonnull
  String MULTI_TENANT = "X-Multi-Tenant";

  /** 用户id头 */
  @Nonnull
  String USER_ID = "X-User-Id";

  /** 租户id头 */
  @Nonnull
  String TENANT_ID = "X-Tenant-Id";

  /** 请求的相对路径 */
  @Nonnull
  String REQUEST_PATH = "X-Request-Path";

  /** 判断是否开启api鉴权的请求头 */
  @Nonnull
  String AUTHENTICATE_API = "X-Authenticate-Api";
}
