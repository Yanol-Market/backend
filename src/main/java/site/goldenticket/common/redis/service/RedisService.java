package site.goldenticket.common.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> type);

    void set(String key, Object value, Long expiredTime);

    boolean setIfAbsent(String key, Object value, Long expiredTime);

    void setMap(String key, Map<String, List<String>> value);

    <T> Map<String, List<T>> getMap(String key, Class<T> type);

    boolean delete(String key);
}
