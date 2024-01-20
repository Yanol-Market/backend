package site.goldenticket.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.api.RestTemplateService;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.domain.security.dto.YanoljaUserResponse;
import site.goldenticket.domain.user.dto.JoinRequest;
import site.goldenticket.domain.user.dto.RegisterAccountRequest;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import static site.goldenticket.common.response.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplateService restTemplateService;

    public boolean isExistEmail(String email) {
        log.info("Duplicated Check Email = {}", email);
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isExistNickname(String nickname) {
        log.info("Duplicated Check Nickname = {}", nickname);
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public Long join(JoinRequest joinRequest) {
        joinValidate(joinRequest);
        log.info("Join User Info = {}", joinRequest);

        String encodePassword = passwordEncoder.encode(joinRequest.password());
        User user = joinRequest.toEntity(encodePassword);
        userRepository.save(user);
        return user.getId();
    }

    public User findById(Long userId) {
        log.info("Find By ID = {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Transactional
    public void registerAccount(Long userId, RegisterAccountRequest registerAccountRequest) {
        User user = findById(userId);
        user.registerAccount(registerAccountRequest.bankName(), registerAccountRequest.accountNumber());
    }

    @Transactional
    public void removeAccount(Long userId) {
        User user = findById(userId);
        user.removeAccount();
    }

    @Transactional
    public Long yanoljaLogin(LoginRequest loginRequest, Long userId) {
        YanoljaUserResponse yanoljaUser = getYanoljaUser(loginRequest);
        User user = findById(userId);
        user.registerYanoljaId(yanoljaUser.id());
        return yanoljaUser.id();
    }

    private void joinValidate(JoinRequest joinRequest) {
        if (isExistEmail(joinRequest.email())) {
            throw new CustomException(ALREADY_EXIST_EMAIL);
        }

        if (isExistNickname(joinRequest.nickname())) {
            throw new CustomException(ALREADY_EXIST_NICKNAME);
        }
    }

    private YanoljaUserResponse getYanoljaUser(LoginRequest loginRequest) {
        return restTemplateService.post(
                "http://localhost:8080/dummy/yauser",
                loginRequest,
                YanoljaUserResponse.class
        ).orElseThrow(() -> new CustomException(LOGIN_FAIL));
    }
}
