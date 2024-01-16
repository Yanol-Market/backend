package site.goldenticket.common.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static site.goldenticket.common.response.ErrorCode.EMPTY_FAILURE_HANDLER;
import static site.goldenticket.common.response.ErrorCode.EMPTY_SUCCESS_HANDLER;

@RequiredArgsConstructor
public class LoginAuthenticationConfigurer<T extends AbstractAuthenticationProcessingFilter>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final T authenticationFilter;

    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    @Override
    public void init(HttpSecurity builder) {
    }

    @Override
    public void configure(HttpSecurity builder) {
        validateHandler();
        setAuthenticationFilter(builder);
        builder.addFilterBefore(
                authenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );
    }

    public LoginAuthenticationConfigurer<T> successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public LoginAuthenticationConfigurer<T> failureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

    private void validateHandler() {
        if (successHandler == null) {
            throw new IllegalStateException(EMPTY_SUCCESS_HANDLER.getMessage());
        }

        if (failureHandler == null) {
            throw new IllegalStateException(EMPTY_FAILURE_HANDLER.getMessage());
        }
    }

    private void setAuthenticationFilter(HttpSecurity builder) {
        authenticationFilter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);
    }
}
