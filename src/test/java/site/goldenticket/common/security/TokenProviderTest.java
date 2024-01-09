package site.goldenticket.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import site.goldenticket.common.security.authentication.TokenProvider;
import site.goldenticket.common.security.authentication.dto.Token;

import static org.assertj.core.api.Assertions.assertThat;
import static site.goldenticket.common.utils.UserUtils.EMAIL;


@DisplayName("Token Provider 검증")
@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("Token 생성 및 Claim 조회 검증")
    void createAccessToken() throws Exception {
        // given
        Token token = tokenProvider.generateToken(EMAIL);

        // when
        String result = tokenProvider.getUsername(token.accessToken());

        // then
        assertThat(result).isEqualTo(EMAIL);
    }
}
