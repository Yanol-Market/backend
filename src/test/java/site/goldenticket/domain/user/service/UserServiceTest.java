package site.goldenticket.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.domain.user.dto.JoinRequest;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static site.goldenticket.common.response.ErrorCode.ALREADY_EXIST_EMAIL;
import static site.goldenticket.common.response.ErrorCode.ALREADY_EXIST_NICKNAME;
import static site.goldenticket.common.utils.UserUtils.*;

@DisplayName("UserService 검증")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
}
