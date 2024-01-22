package site.goldenticket.domain.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.goldenticket.common.api.RestTemplateService;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.common.security.authentication.token.TokenService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.security.dto.ReissueRequest;
import site.goldenticket.domain.security.dto.YanoljaLoginResponse;
import site.goldenticket.domain.security.dto.YanoljaUserResponse;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.util.UUID;

import static site.goldenticket.common.response.ErrorCode.LOGIN_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RestTemplateService restTemplateService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(LOGIN_FAIL.getMessage()));

        log.info("LoadUser User = {}", user);
        return new PrincipalDetails(user);
    }

    public AuthenticationToken reissue(ReissueRequest reissueRequest) {
        return tokenService.reissueToken(reissueRequest.refreshToken());
    }

    public YanoljaLoginResponse yanoljaLogin(LoginRequest loginRequest) {
        YanoljaUserResponse yanoljaUser = fetchYanoljaUser(loginRequest);
        log.info("Yanolja Login Info = {}", yanoljaUser);

        return createYanoljaLoginResponse(yanoljaUser);
    }

    private YanoljaUserResponse fetchYanoljaUser(LoginRequest loginRequest) {
        return restTemplateService.post(
                "http://localhost:8080/dummy/yauser",
                loginRequest,
                YanoljaUserResponse.class
        ).orElseThrow(() -> new CustomException(LOGIN_FAIL));
    }

    private YanoljaLoginResponse createYanoljaLoginResponse(YanoljaUserResponse yanoljaUser) {
        boolean isFirst = !userRepository.existsByYanoljaId(yanoljaUser.id());
        AuthenticationToken token = generateToken(yanoljaUser.email());
        return YanoljaLoginResponse.builder()
                .isFirst(isFirst)
                .userInfo(yanoljaUser)
                .token(token)
                .build();
    }

    private AuthenticationToken generateToken(String email) {
        log.info("Yanolja Login Email = {}", email);
        String randomToken = UUID.randomUUID().toString();
        return tokenService.generatedToken(randomToken, email);
    }
}
