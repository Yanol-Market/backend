package site.goldenticket.common.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import site.goldenticket.common.exception.CustomException;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static site.goldenticket.common.response.ErrorCode.COMMON_INVALID_PARAMETER;
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

        if (serializedValue == null) {
            log.error("Serialized value is null for key: {}", key);
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(serializedValue, type));
        } catch (IllegalArgumentException | InvalidFormatException e) {
            throw new CustomException(COMMON_INVALID_PARAMETER);
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
            throw new CustomException("Redis getListRange() Error", COMMON_SYSTEM_ERROR);
        }
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
    public Double getZScore(String rankingKey, String keyword) {
        Double score = redisTemplate.opsForZSet().score(rankingKey, keyword);
        log.info("Get ZScore - Ranking Key: {}, Keyword: {}, Score: {}", rankingKey, keyword, score);
        return score;
    }

    @Override
    public void addZScore(String rankingKey, String keyword, Double newScore) {
        redisTemplate.opsForZSet().add(rankingKey, keyword, newScore);
        log.info("Add ZScore - Ranking Key: {}, Keyword: {}, New Score: {}", rankingKey, keyword, newScore);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getZRanking(String rankingKey, long start, long end) {
        Set<ZSetOperations.TypedTuple<String>> ranking = redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, start, end);
        log.info("Get ZRanking - Ranking Key: {}, Start: {}, End: {}, Size: {}", rankingKey, start, end, ranking.size());
        return ranking;
    }

    @Override
    public Long opsForListSize(String key) {
        Long size = redisTemplate.opsForList().size(key);
        log.info("Get List Size - Key: {}, Size: {}", key, size);
        return size;
    }

    @Override
    public Object rightPop(String key) {
        Object poppedValue = redisTemplate.opsForList().rightPop(key);
        log.info("Right Pop from List - Key: {}, Popped Value: {}", key, poppedValue);
        return poppedValue;
    }

    @Override
    public void leftPush(String key, Object value) {
        try {
            String serializedValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForList().leftPush(key, serializedValue);
            log.info("Left Push to List - Key: {}, Value: {}", key, serializedValue);
        } catch (Exception e) {
            log.error("Redis Left Push Exception", e);
            throw new CustomException("Redis get() Error", COMMON_SYSTEM_ERROR);
        }
    }
}
