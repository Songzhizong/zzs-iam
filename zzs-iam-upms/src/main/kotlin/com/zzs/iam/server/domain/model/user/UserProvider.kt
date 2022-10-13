package com.zzs.iam.server.domain.model.user

import cn.idealframework2.exception.BadRequestException
import cn.idealframework2.exception.ResourceNotFoundException

/**
 * @author 宋志宗 on 2022/8/16
 */
interface UserProvider {

  /**
   * 通过用户名密码对用户进行身份认证, 认证成功返回用户信息
   *
   * @param username 用户名
   * @param password 密码
   */
  @Throws(BadRequestException::class)
  suspend fun authenticate(username: String, password: String): AuthUser

  /**
   * 通过用户id获取用户信息
   *
   * @param id 用户唯一id
   */
  @Throws(ResourceNotFoundException::class)
  suspend fun getById(id: String): AuthUser

  /**
   * 通过用户id列表批量获取用户信息
   *
   * @param ids 用户id列表
   */
  suspend fun findAllById(ids: Collection<String>): List<AuthUser>

  /**
   * 通过手机号获取用户信息
   *
   * @param phone 手机号
   */
  suspend fun getByPhone(phone: String): AuthUser
}
