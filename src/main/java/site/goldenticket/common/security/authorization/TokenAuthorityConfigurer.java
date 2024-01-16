package site.goldenticket.common.security.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import site.goldenticket.common.security.authentication.token.TokenService;

@RequiredArgsConstructor
public class TokenAuthorityConfigurer
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public void init(HttpSecurity builder) {
    }

    @Override
    public void configure(HttpSecurity builder) {
        builder.addFilterBefore(
                new TokenAuthorizationFilter(tokenService, userDetailsService),
                AuthorizationFilter.class
        );
    }
}
