package site.goldenticket.common.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import site.goldenticket.common.exception.CustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static site.goldenticket.common.response.ErrorCode.COMMON_SYSTEM_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        log.info("Find Redis Key = [{}], Type = [{}]", key, type.getName());
        String serializedValue = redisTemplate.opsForValue().get(key);

        try {
            return Optional.of(objectMapper.readValue(serializedValue, type));
        } catch (IllegalArgumentException | InvalidFormatException e) {
            log.warn("[{}] Not Exist Value", key);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Redis Get Exception", e);
            throw new CustomException("Redis get() Error", COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public void set(String key, Object value, Long expiredTime) {
        log.info("Save Redis Key = [{}], Value = [{}], expiredTime = [{}]", key, value, expiredTime);
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, serializedValue, expiredTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis Set Exception", e);
            throw new CustomException("Redis get() Error", COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Long expiredTime) {
        log.info("Save If Absent Redis Key = [{}], Value = [{}], expiredTime = [{}]", key, value, expiredTime);
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            return Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(
                            key,
                            serializedValue,
                            expiredTime, TimeUnit.SECONDS
                    ));
        } catch (Exception e) {
            log.error("Redis Set Exception", e);
            throw new CustomException("Redis get() Error", COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public boolean delete(String key) {
        log.info("Delete Redis Key = [{}]", key);
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public <T> List<T> getList(String key, Class<T> type) {
        log.info("Get Range from Redis List - Key: [{}], Type: [{}]", key, type.getName());
        try {
            List<String> serializedList = redisTemplate.opsForList().range(key, 0, -1);
            List<T> deserializedList = new ArrayList<>();

            for (String serializedValue : serializedList) {
                T deserializedValue = objectMapper.readValue(serializedValue, type);
                deserializedList.add(deserializedValue);
            }

            return deserializedList;
        } catch (Exception e) {
            log.error("Redis Get List Range Exception", e);
            throw new CustomException("Redis getList() Error", COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public void leftPush(String key, Object value) {
        try {
            log.info("Left Push to List - Key: [{}]", key);
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForList().leftPush(key, serializedValue);
        } catch (Exception e) {
            log.error("Redis Left Push Exception", e);
            throw new CustomException("Redis getLeftPush() Error", COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public void rightPop(String key) {
        log.info("Right Pop from List - Key: [{}]", key);
        redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public void removeList(String key, Object value) {
        log.info("Remove from Redis List - Key: [{}], Old Value: [{}]", key, value);
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForList().remove(key, 0, serializedValue);
        } catch (Exception e) {
            log.error("Redis Remove List Exception", e);
            throw new CustomException("Redis removeList() Error", COMMON_SYSTEM_ERROR);
        }
    }

    @Override
    public Double getZScore(String key, String keyword) {
        log.info("Get ZScore - Ranking Key: [{}], Keyword: [{}]", key, keyword);
        return redisTemplate.opsForZSet().score(key, keyword);
    }

    @Override
    public void addZScore(String key, String keyword, Double score) {
        log.info("Add ZScore - Ranking Key: [{}], Keyword: [{}], Score: [{}]", key, keyword, score);
        redisTemplate.opsForZSet().add(key, keyword, score);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getZRank(String key, long start, long end) {
        log.info("Get ZRanking - Ranking Key: [{}], Start: [{}], End: [{}]", key, start, end);
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    @Override
    public Set<String> getZRangeByLex(String key, String prefix, int limit) {
        log.info("Get ZRangeByLex - Key: [{}], Prefix: [{}], Limit: [{}]", key, prefix, limit);
        RedisZSetCommands.Range range = RedisZSetCommands.Range.range().gte(prefix).lte(prefix + Character.MAX_VALUE);
        Set<String> autoCompletedWords = redisTemplate.opsForZSet().rangeByLex(key, range.toRange());
        return autoCompletedWords.stream().limit(limit).collect(Collectors.toSet());
    }
}
