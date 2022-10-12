//package com.zzs.iam.gateway.common
//
//import com.github.benmanes.caffeine.cache.Caffeine
//import org.springframework.http.server.PathContainer
//import org.springframework.web.util.pattern.PathPattern
//import org.springframework.web.util.pattern.PathPatternParser
//import java.time.Duration
//import java.util.concurrent.ConcurrentHashMap
//
///**
// * @author 宋志宗 on 2022/9/27
// */
//object PathMatchers {
//  private val pathPatterns = ConcurrentHashMap<String, PathPattern>()
//  private val pathContainerCache = Caffeine.newBuilder()
//    .expireAfterAccess(Duration.ofMinutes(10))
//    .expireAfterWrite(Duration.ofMinutes(60))
//    .maximumSize(10000)
//    .build<String, PathContainer>()
//
//  fun match(pattern: String, path: String): Boolean {
//    val pathPattern = parsePattern(pattern)
//    val container = parsePath(path)
//    return pathPattern.matches(container)
//  }
//
//  private fun parsePath(path: String): PathContainer = pathContainerCache.get(path) {
//    PathContainer.parsePath(path)
//  }
//
//  private fun parsePattern(pattern: String) = pathPatterns.computeIfAbsent(pattern) {
//    PathPatternParser.defaultInstance.parse(it)
//  }
//}
