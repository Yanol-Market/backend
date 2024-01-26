package site.goldenticket.domain.user.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.goldenticket.common.exception.CustomException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static site.goldenticket.common.response.ErrorCode.*;
import static site.goldenticket.common.utils.UserUtils.*;

@DisplayName("사용자 도메인 검증")
class UserTest {

    User user;

    @BeforeEach
    void setUp() {
        user = createUser(PASSWORD);
    }

    @Test
    @DisplayName("야놀자 ID 등록 검증")
    void registerYanoljaId() {
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

    @Test
    @DisplayName("계좌 등록 검증")
    void registerAccount() {
        // given
        String bankName = BANK_NAME;
        String accountNumber = ACCOUNT_NUMBER;

        // when
        user.registerAccount(bankName, accountNumber);

        // then
        assertThat(user.getBankName()).isEqualTo(BANK_NAME);
        assertThat(user.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    @DisplayName("계좌 검증 실패 - 계좌가 이미 등록된 경우 예외 발생")
    void registerAccount_failureAlreadyRegister() {
        // given
        user.registerAccount(BANK_NAME, ACCOUNT_NUMBER);

        // when
        // then
        assertThatThrownBy(() -> user.registerAccount(BANK_NAME, ACCOUNT_NUMBER))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_REGISTER_ACCOUNT.getMessage());
    }
}
