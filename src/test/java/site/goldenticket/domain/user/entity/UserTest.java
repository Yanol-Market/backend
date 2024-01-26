package site.goldenticket.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.goldenticket.common.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static site.goldenticket.common.response.ErrorCode.ALREADY_REGISTER_YANOLJA_ID;
import static site.goldenticket.common.utils.UserUtils.*;

@DisplayName("사용자 도메인 검증")
class UserTest {

    @Test
    @DisplayName("야놀자 ID 등록 검증")
    void registerYanoljaId() {
        // given
        User user = createUser(PASSWORD);

        // when
        user.registerYanoljaId(YANOLJA_ID);

        // then
        assertThat(user.getYanoljaId()).isEqualTo(YANOLJA_ID);
    }

    @Test
    @DisplayName("야놀자 ID 등록 실패 - 이미 야놀자 ID가 등록된 경우 예외 발생")
    void registerYanoljaId_failureAlreadyRegister() {
        // given
        User user = createUserWithYanolja(PASSWORD);

        // when
        // then
        assertThatThrownBy(() -> user.registerYanoljaId(YANOLJA_ID))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_REGISTER_YANOLJA_ID.getMessage());
    }
}
