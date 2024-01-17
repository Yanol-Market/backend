package site.goldenticket.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import site.goldenticket.common.security.authentication.*;
import site.goldenticket.common.security.authentication.token.TokenService;
import site.goldenticket.common.security.authorization.SecurityAccessDeniedHandler;
import site.goldenticket.common.security.authorization.SecurityAuthenticationEntryPoint;
import site.goldenticket.common.security.authorization.TokenAuthorityConfigurer;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] PERMIT_ALL_URLS = new String[]{
            "/h2-console/**",
            "/dummy/**"
    };

    private static final String[] PERMIT_ALL_GET_URLS = new String[]{
            "/favicon.ico",
            "/docs/**",
            "/users/check/**",
            "/products/**",
            "/home"
    };

    private static final String[] PERMIT_ALL_POST_URLS = new String[]{
            "/users",
            "/reissue",
            "/yanolja-login",
            "/chats/test"
    };

    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(createCorsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_ALL_URLS).permitAll()
                        .requestMatchers(GET, PERMIT_ALL_GET_URLS).permitAll()
                        .requestMatchers(POST, PERMIT_ALL_POST_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logoutConfigurer -> logoutConfigurer
                        .addLogoutHandler(createLogoutHandler())
                        .logoutSuccessHandler(createLogoutSuccessHandler())
                )
                .with(
                        new LoginAuthenticationConfigurer<>(createAuthenticationFilter()),
                        SecurityAuthenticationFilter -> SecurityAuthenticationFilter
                                .successHandler(createAuthenticationSuccessHandler())
                                .failureHandler(createAuthenticationFailureHandler())
                )
                .with(
                        new TokenAuthorityConfigurer(tokenService, userDetailsService),
                        Customizer.withDefaults()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(createAccessDeniedHandler())
                        .authenticationEntryPoint(createAuthenticationEntryPoint())
                );

        return http.getOrBuild();
    }

    @Bean
    public CorsConfigurationSource createCorsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://golden-ticket6.netlify.app"
        ));
        corsConfiguration.setAllowedMethods(List.of(
                GET.name(),
                POST.name(),
                PUT.name(),
                PATCH.name(),
                DELETE.name(),
                OPTIONS.name()
        ));
        corsConfiguration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    private AbstractAuthenticationProcessingFilter createAuthenticationFilter() {
        return new LoginAuthenticationFilter(objectMapper);
    }

    private AuthenticationSuccessHandler createAuthenticationSuccessHandler() {
        return new LoginAuthenticationSuccessHandler(objectMapper, tokenService);
    }

    private LogoutHandler createLogoutHandler() {
        return new LogoutTokenHandler(objectMapper, tokenService);
    }

    private LogoutSuccessHandler createLogoutSuccessHandler() {
        return new LogoutTokenSuccessHandler(objectMapper);
    }

    private AuthenticationFailureHandler createAuthenticationFailureHandler() {
        return new LoginAuthenticationFailureHandler(objectMapper);
    }

    private AccessDeniedHandler createAccessDeniedHandler() {
        return new SecurityAccessDeniedHandler();
    }

    private AuthenticationEntryPoint createAuthenticationEntryPoint() {
        return new SecurityAuthenticationEntryPoint(objectMapper);
    }
}
