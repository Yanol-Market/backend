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
import site.goldenticket.domain.user.dto.ChangeProfileRequest;
import site.goldenticket.domain.user.dto.JoinRequest;
import site.goldenticket.domain.user.dto.RemoveUserRequest;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
}
