package site.goldenticket.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.goldenticket.common.redis.service.RedisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Redis Service 검증")
@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    @DisplayName("데이터 삽입 조회 검증")
    void redisSetAndGet() {
        // given
        String key = "key";
        String value = "value";

        // when
        redisService.set(key, value, 1L);

        // then
        String result = redisService.get(key, String.class).orElse(null);
        assertThat(result).isEqualTo(value);
    }

    @ParameterizedTest
    @CsvSource({"key,false", "other,true"})
    @DisplayName("이미 존재하는 Key 삽입 검증")
    void redisExistKey(String key, boolean expected) {
        // given
        redisService.set("key", "value", 1L);

        // when
        boolean result = redisService.setIfAbsent(key, "value", 1L);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"key,true", "param,false"})
    @DisplayName("데이터 삭제 검증")
    void redisDelete(String key, boolean expected) {
        // given
        redisService.set("key", "value", 1L);

        // when
        boolean result = redisService.delete(key);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
