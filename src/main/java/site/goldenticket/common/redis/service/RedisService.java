package site.goldenticket.common.redis.service;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RedisService {

    <T> Optional<T> get(String key, Class<T> type);

    void set(String key, Object value, Long expiredTime);

    boolean setIfAbsent(String key, Object value, Long expiredTime);

    boolean delete(String key);

    <T> List<T> getList(String key, Class<T> type);

    void rightPop(String key);

    void leftPush(String key, Object value);

    void removeList(String key, Object value);

    Double getZScore(String key, String keyword);

    void addZScore(String key, String keyword, Double score);

    Set<ZSetOperations.TypedTuple<String>> getZRank(String key, long start, long end);

    Set<String> getZRangeByLex(String key, String prefix, int limit);
}
