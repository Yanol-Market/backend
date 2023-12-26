package site.goldenticket.common.redis.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Profile({"default", "local"})
@Configuration
@RequiredArgsConstructor
public class EmbeddedRedisConfig {

    private static final String REDIS_DOCKER_IMAGE = "redis:7-alpine";

    static {
        GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS_CONTAINER.start();

        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
    }
}
