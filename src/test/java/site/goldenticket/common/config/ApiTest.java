package site.goldenticket.common.config;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.goldenticket.common.security.authentication.token.TokenProvider;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.util.UUID;

import static site.goldenticket.common.utils.UserUtils.*;

@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @LocalServerPort
    private int port;

    public User user;
    public String accessToken;

    @BeforeEach
    void init() {
        RestAssured.port = port;
        user = saveUser();
        accessToken = getAccessToken();
    }

    private User saveUser() {
        String encodePassword = passwordEncoder.encode(PASSWORD);
        User user = createUser(encodePassword);
        userRepository.save(user);
        return user;
    }

    private String getAccessToken() {
        return tokenProvider.generateToken(
                UUID.randomUUID().toString(),
                EMAIL
        ).accessToken();
    }
}
