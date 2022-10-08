package com.zzs.iam.gateway.filter.blacklist;

import cn.idealframework2.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 宋志宗 on 2022/9/20
 */
public class BlacklistProperties {
  private Set<String> blacklist = new HashSet<>();

  public Set<String> getParsedBlacklist() {
    Set<String> parsedBlacklist = new HashSet<>();
    for (String s : blacklist) {
      if (StringUtils.isBlank(s)) {
        continue;
      }
      if (!s.contains("/")) {
        parsedBlacklist.add(s);
      }

    }
    return parsedBlacklist;
  }

  public Set<String> getBlacklist() {
    return blacklist;
  }

  public BlacklistProperties setBlacklist(Set<String> blacklist) {
    this.blacklist = blacklist;
    return this;
  }
}
