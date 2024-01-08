package site.goldenticket.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import site.goldenticket.common.security.authentication.AuthenticationConfigurer;
import site.goldenticket.common.security.authentication.SecurityAuthenticationFailureHandler;
import site.goldenticket.common.security.authentication.SecurityAuthenticationFilter;
import site.goldenticket.common.security.authentication.SecurityAuthenticationSuccessHandler;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] PERMIT_ALL_GET_URLS = new String[]{
            "/favicon.ico",
            "/docs/**"
    };

    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(GET, PERMIT_ALL_GET_URLS).permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated()
                ).with(
                        new AuthenticationConfigurer<>(createAuthenticationFilter()),
                        SecurityAuthenticationFilter -> SecurityAuthenticationFilter
                                .successHandler(createAuthenticationSuccessHandler())
                                .failureHandler(createAuthenticationFailureHandler())
                );

        return http.getOrBuild();
    }

    private AbstractAuthenticationProcessingFilter createAuthenticationFilter() {
        return new SecurityAuthenticationFilter(objectMapper);
    }

    private AuthenticationSuccessHandler createAuthenticationSuccessHandler() {
        return new SecurityAuthenticationSuccessHandler(objectMapper);
    }

    private AuthenticationFailureHandler createAuthenticationFailureHandler() {
        return new SecurityAuthenticationFailureHandler(objectMapper);
    }
}
