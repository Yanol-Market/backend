package site.goldenticket.common.redis.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public <T> Map<String, List<T>> getMap(String key, Class<T> type) {
        log.info("Get Map from Redis Key = [{}]", key);
        Map<Object, Object> serializedMap = redisTemplate.opsForHash().entries(key);
        Map<String, List<T>> deserializedMap = new HashMap<>();

        for (Map.Entry<Object, Object> mapEntry : serializedMap.entrySet()) {
            String mapKey = mapEntry.getKey().toString();
            String mapValue = mapEntry.getValue().toString();

            try {
                JavaType valueType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
                List<T> deserializedList = objectMapper.readValue(mapValue, valueType);
                deserializedMap.put(mapKey, deserializedList);
                log.info("Get Map Entry - Key: [{}], Values: [{}]", mapKey, deserializedList);
            } catch (IllegalArgumentException | InvalidFormatException e) {
                throw new CustomException(COMMON_INVALID_PARAMETER);
            } catch (Exception e) {
                log.error("Redis Get Map Exception", e);
                throw new CustomException("Redis get() Error", COMMON_SYSTEM_ERROR);
            }
        }
        return deserializedMap;
    }

    @Override
    public void setMap(String key, Map<String, List<String>> value) {
        log.info("Save Map to Redis Key = [{}], Value = [{}]", key, value);

        for (Map.Entry<String, List<String>> valueEntry : value.entrySet()) {
            String entryKey = valueEntry.getKey();
            List<String> entryValue = valueEntry.getValue();

            try {
                String serializedValue = objectMapper.writeValueAsString(entryValue);
                redisTemplate.opsForHash().put(key, entryKey, serializedValue);
            } catch (Exception e) {
                log.error("Redis Set Map with List Values Exception", e);
                throw new CustomException("Redis setMapWithListValues() Error", COMMON_SYSTEM_ERROR);
            }
        }
    }

    @Override
    public boolean delete(String key) {
        log.info("Delete Redis Key = [{}]", key);
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
