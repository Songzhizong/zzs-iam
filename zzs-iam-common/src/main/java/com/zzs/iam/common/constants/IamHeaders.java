package com.zzs.iam.common.constants;

import javax.annotation.Nonnull;

/**
 * Http头
 *
 * @author 宋志宗 on 2022/8/16
 */
public interface IamHeaders {

  /** 平台编码, String */
  @Nonnull
  String PLATFORM = "X-Platform";

  /** 是否多租户平台, Boolean */
  @Nonnull
  String MULTI_TENANT = "X-Multi-Tenant";

  /** 用户id头, Long */
  @Nonnull
  String USER_ID = "X-User-Id";

  /** 租户id头, Long */
  @Nonnull
  String TENANT_ID = "X-Tenant-Id";

  /** 请求的相对路径, String */
  @Nonnull
  String REQUEST_PATH = "X-Request-Path";

  /** 判断是否开启api鉴权的请求头, Boolean */
  @Nonnull
  String AUTHENTICATE_API = "X-Authenticate-Api";
}
