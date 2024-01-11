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

    Double getZScore(String rankingKey, String keyword);

    void addZScore(String rankingKey, String keyword, Double newScore);

    Set<ZSetOperations.TypedTuple<String>> getZRanking(String rankingKey, long start, long end);

    Long opsForListSize(String key);

    Object rightPop(String key);

    void leftPush(String key, Object value);
}
