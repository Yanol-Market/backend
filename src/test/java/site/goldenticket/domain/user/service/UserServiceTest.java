package site.goldenticket.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.user.dto.*;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static site.goldenticket.common.response.ErrorCode.*;
import static site.goldenticket.common.utils.UserUtils.*;
import static site.goldenticket.common.utils.UserUtils.createChangeProfileRequest;

@DisplayName("UserService 검증")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final long USER_ID = -1L;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 검증")
    void join() {
        // given
        when(userRepository.findByEmail(JOIN_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.existsByNickname(JOIN_NICKNAME)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodePassword");
        JoinRequest joinRequest = createJoinRequest();

        // when
        userService.join(joinRequest);

        // then
        verify(userRepository, atMostOnce()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일인 경우 예외 발생")
    void join_failureDuplicateEmail() {
        // given
        when(userRepository.findByEmail(JOIN_EMAIL)).thenReturn(Optional.of(createUser(PASSWORD)));
        JoinRequest joinRequest = createJoinRequest();

        // when
        // then
        assertThatThrownBy(() -> userService.join(joinRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_EXIST_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임인 경우 예외 발생")
    void join_failureDuplicateNickname() {
        // given
        when(userRepository.findByEmail(JOIN_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.existsByNickname(JOIN_NICKNAME)).thenReturn(true);
        JoinRequest joinRequest = createJoinRequest();

        // when
        // then
        assertThatThrownBy(() -> userService.join(joinRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_EXIST_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원 삭제 검증")
    void deleteUser() {
        // given
        User user = createUser(PASSWORD);
        ReflectionTestUtils.setField(user, "id", USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        RemoveUserRequest removeUserRequest = createRemoveUserRequest();

        // when
        userService.deleteUser(USER_ID, removeUserRequest);

        // then
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("회원 삭제 실패 - 사용자 정보가 없는 경우 예외 발생")
    void deleteUser_failureNotFoundUser() {
        // given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        RemoveUserRequest removeUserRequest = createRemoveUserRequest();

        // when
        // then
        assertThatThrownBy(() -> userService.deleteUser(USER_ID, removeUserRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }
    
    @Test
    @DisplayName("프로필 수정 검증")
    void updateProfile() {
        // given
        User user = createUser(PASSWORD);
        ReflectionTestUtils.setField(user, "id", USER_ID);
        
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        ChangeProfileRequest changeProfileRequest = createChangeProfileRequest();

        // when
        userService.updateProfile(USER_ID, changeProfileRequest);
        
        // then
        assertThat(user.getNickname()).isEqualTo(CHANGE_NICKNAME);
    }

    @Test
    @DisplayName("프로필 수정 실패 - 중복된 닉네임인 경우 예외 발생")
    void updateProfile_failureDuplicateNickname() {
        // given
        when(userRepository.existsByNickname(CHANGE_NICKNAME)).thenReturn(true);
        ChangeProfileRequest changeProfileRequest = createChangeProfileRequest();

        // when
        // then
        assertThatThrownBy(() -> userService.updateProfile(USER_ID, changeProfileRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_EXIST_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("비밀번호 수정 검증")
    void updatePassword() {
        // given
        User user = createUser(PASSWORD);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(CHANGE_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        ChangePasswordRequest changePasswordRequest = createChangePasswordRequest();

        // when
        userService.updatePassword(USER_ID, changePasswordRequest);

        // then
        assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 기존 비밀번호가 틀린 경우 예외 발생")
    void updatePassword_failureInvalidPassword() {
        // given
        User user = createUser(PASSWORD);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(false);
        ChangePasswordRequest changePasswordRequest = createChangePasswordRequest();

        // when
        // then
        assertThatThrownBy(() -> userService.updatePassword(USER_ID, changePasswordRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("계좌 등록 검증")
    void registerAccount() {
        // given
        User user = createUser(PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        RegisterAccountRequest registerAccountRequest = createRegisterAccountRequest();

        // when
        userService.registerAccount(USER_ID, registerAccountRequest);

        // then
        assertAll(
                () -> assertThat(user.getBankName()).isEqualTo(BANK_NAME),
                () -> assertThat(user.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER)
        );
    }

    @Test
    @DisplayName("계좌 등록 실패 - 이미 계좌가 등록된 경우 예외 발생")
    void registerAccount_failureAlreadyRegisterAccount() {
        // given
        User user = createUserWithAccount(PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        RegisterAccountRequest registerAccountRequest = createRegisterAccountRequest();

        // when
        // then
        assertThatThrownBy(() -> userService.registerAccount(USER_ID, registerAccountRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_REGISTER_ACCOUNT.getMessage());
    }

    @Test
    @DisplayName("계좌 등록 실패 - 등록 계좌정보가 없는 경우 예외 발생")
    void registerAccount_failureDuplicateNickname() {
        // given
        User user = createUser(PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        RegisterAccountRequest registerAccountRequest = new RegisterAccountRequest("", "");

        // when
        // then
        assertThatThrownBy(() -> userService.registerAccount(USER_ID, registerAccountRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_REGISTER_ACCOUNT_PARAM.getMessage());
    }

    @Test
    @DisplayName("계좌 삭제 검증")
    void removeAccount() {
        // given
        User user = createUser(PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        userService.removeAccount(USER_ID);

        // then
        assertAll(
                () -> assertThat(user.getBankName()).isNull(),
                () -> assertThat(user.getAccountNumber()).isNull()
        );
    }
}
