package site.goldenticket.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.goldenticket.common.security.authentication.token.TokenProvider;
import site.goldenticket.common.security.authentication.token.dto.Token;

import static org.assertj.core.api.Assertions.assertThat;
import static site.goldenticket.common.utils.UserUtils.EMAIL;


@DisplayName("Token Provider 검증")
@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("Token 생성 및 Claim 조회 검증")
    void createAccessToken() {
        // given
        Token token = tokenProvider.generateToken("randomToken", EMAIL);

        // when
        String result = tokenProvider.getUsername(token.accessToken());

        // then
        assertThat(result).isEqualTo(EMAIL);
    }
}
