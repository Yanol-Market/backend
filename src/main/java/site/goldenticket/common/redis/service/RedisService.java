package site.goldenticket.common.redis.service;

import java.util.Optional;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> type);

    void set(String key, Object value, Long expiredTime);

    boolean setIfAbsent(String key, Object value, Long expiredTime);

    boolean delete(String key);
}