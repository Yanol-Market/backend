package site.goldenticket.common.security.authorization;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import site.goldenticket.common.security.authentication.TokenProvider;
import site.goldenticket.common.security.exception.InvalidJwtException;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static site.goldenticket.common.response.ErrorCode.INVALID_TOKEN;
import static site.goldenticket.common.security.authorization.SecurityErrorCode.ERROR_KEY;
import static site.goldenticket.common.security.authorization.SecurityErrorCode.TOKEN_EXPIRED;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private static final String GRANT_TYPE = "Bearer ";

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("requestURI = {}", request.getRequestURI());

        String authorization = getAuthorization(request);
        if (isValidToken(authorization)) {
            String token = authorization.substring(GRANT_TYPE.length());
            log.info("accessToken = {}", token);

            try {
                String username = tokenProvider.getUsername(token);
                log.info("Authority username = {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Authentication authenticated = createAuthentication(userDetails);
                setSecurityContext(authenticated);
            } catch (ExpiredJwtException e) {
               request.setAttribute(ERROR_KEY, TOKEN_EXPIRED);
            } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
                throw new InvalidJwtException(INVALID_TOKEN, e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getAuthorization(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    private boolean isValidToken(String token) {
        return StringUtils.hasText(token) && token.startsWith(GRANT_TYPE);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(UserDetails userDetails) {
        return UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    private void setSecurityContext(Authentication authenticated) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
    }
}
