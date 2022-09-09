package com.zzs.iam.common.infrastructure.webflux;

import reactor.util.context.Context;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * @author 宋志宗 on 2022/8/16
 */
public class WebFluxContext implements Context {
  private final ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>();

  @Nonnull
  @Override
  public Context put(@Nonnull Object key, @Nonnull Object value) {
    map.put(key, value);
    return this;
  }

  @Nonnull
  @Override
  public Context delete(@Nonnull Object key) {
    return this;
  }


  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <T> T get(@Nonnull Object key) {
    Object o = map.get(key);
    if (o == null) {
      throw new NoSuchElementException();
    }
    return (T) o;
  }

  @Override
  public boolean hasKey(@Nonnull Object key) {
    return map.containsKey(key);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Nonnull
  @Override
  public Stream<Map.Entry<Object, Object>> stream() {
    return map.entrySet().stream();
  }
}
