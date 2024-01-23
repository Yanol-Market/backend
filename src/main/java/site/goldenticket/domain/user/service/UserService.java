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
import site.goldenticket.domain.user.dto.*;
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
    private final EmailService emailService;

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
    public void updateProfile(Long userId, ChangeProfileRequest changeProfileRequest) {
        log.info("User ID [{}] Change Profile = {}", userId, changeProfileRequest);
        updateProfileValidate(changeProfileRequest);
        User user = findById(userId);
        user.updateProfile(changeProfileRequest.nickname());
    }

    @Transactional
    public void updatePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        User user = findById(userId);
        log.info("Change Password User = {}", user);

        updatePasswordValidate(user, changePasswordRequest);
        String encodePassword = passwordEncoder.encode(changePasswordRequest.changePassword());
        user.updatePassword(encodePassword);
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

        duplicateNickname(joinRequest.nickname());
    }

    private void updateProfileValidate(ChangeProfileRequest changeProfileRequest) {
        duplicateNickname(changeProfileRequest.nickname());
    }

    private void duplicateNickname(String nickname) {
        if (isExistNickname(nickname)) {
            throw new CustomException(ALREADY_EXIST_NICKNAME);
        }
    }

    private void updatePasswordValidate(User user, ChangePasswordRequest changePasswordRequest) {
        if (!passwordEncoder.matches(changePasswordRequest.originPassword(), user.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }
    }

    private YanoljaUserResponse getYanoljaUser(LoginRequest loginRequest) {
        return restTemplateService.post(
                "http://localhost:8080/dummy/yauser",
                loginRequest,
                YanoljaUserResponse.class
        ).orElseThrow(() -> new CustomException(LOGIN_FAIL));
    }

    @Transactional
    public void resetPasswordAndSendEmail(ResetPasswordRequest resetPasswordRequest) {
        String newPassword = getTempPassword();
        String hashedPassword = passwordEncoder.encode(newPassword);

        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new CustomException("존재하지 않는 유저입니다.",USER_NOT_FOUND));

        user.setPassword(hashedPassword);

        emailService.sendPasswordResetEmail(resetPasswordRequest.getEmail(), newPassword);
    }

    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }
}
