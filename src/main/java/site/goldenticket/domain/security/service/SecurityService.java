package site.goldenticket.domain.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.security.authentication.dto.AuthenticationToken;
import site.goldenticket.common.security.authentication.dto.LoginRequest;
import site.goldenticket.common.security.authentication.token.TokenService;
import site.goldenticket.domain.security.PrincipalDetails;
import site.goldenticket.domain.security.dto.YanoljaUserResponse;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.repository.UserRepository;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static site.goldenticket.common.response.ErrorCode.LOGIN_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(LOGIN_FAIL.getMessage()));

        log.info("LoadUser User = {}", user);
        return new PrincipalDetails(user);
    }

    public YanoljaUserResponse fetchYanoljaUser(LoginRequest loginRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        try {
            return restTemplate.postForObject("http://localhost:8080/dummy/yauser", request, YanoljaUserResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("Yanolja API Connect Error Message = {}", e.getMessage());
            throw new CustomException(LOGIN_FAIL);
        }
    }

    public AuthenticationToken generateToken(Long yanoljaId) {
        User user = userRepository.findByYanoljaId(yanoljaId)
                .orElseThrow(() -> new CustomException(LOGIN_FAIL));

        String email = user.getEmail();
        log.info("Yanolja Login Email = {}", email);

        String randomToken = UUID.randomUUID().toString();
        return tokenService.generatedToken(randomToken, email);
    }
}
