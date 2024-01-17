package site.goldenticket.common.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.security.authentication.dto.LogoutRequest;
import site.goldenticket.common.security.authentication.token.TokenService;

import java.io.IOException;

import static site.goldenticket.common.response.ErrorCode.COMMON_INVALID_PARAMETER;

@Slf4j
@RequiredArgsConstructor
public class LogoutTokenHandler implements LogoutHandler {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        LogoutRequest logoutRequest = getLogoutInfo(request);
        log.info("Logout Info = {}", logoutRequest);
        tokenService.removeRefreshToken(logoutRequest.refreshToken());
        tokenService.addBlackList(logoutRequest.accessToken());
    }

    private LogoutRequest getLogoutInfo(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getReader(), LogoutRequest.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomException(COMMON_INVALID_PARAMETER);
        }
    }
}
