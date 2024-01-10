package site.goldenticket.common.redis.service;

import java.util.List;
import java.util.Optional;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> type);

    void set(String key, Object value, Long expiredTime);

    boolean setIfAbsent(String key, Object value, Long expiredTime);

    <T> List<T> getList(String key, Class<T> elementType);

    void setList(String key, Object value);

    boolean delete(String key);
}
