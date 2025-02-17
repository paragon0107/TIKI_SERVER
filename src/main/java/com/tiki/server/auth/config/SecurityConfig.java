package com.tiki.server.auth.config;

import com.tiki.server.auth.exception.handler.CustomAuthenticationEntryPointHandler;
import com.tiki.server.auth.filter.ExceptionHandlerFilter;
import com.tiki.server.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITE_LIST = {
            "/api/v1/auth/sign-in",
            "/api/v1/auth/reissue",
            "/api/v1/members/password",
            "/api/v1/members",
            "/api/v1/mail/**",
            "/actuator/health"
    };

    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        permitSwaggerUri(http);
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .authenticationEntryPoint(customAuthenticationEntryPointHandler))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(AUTH_WHITE_LIST).permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        exceptionHandlerFilter, JwtAuthenticationFilter.class
                )
                .build();
    }

    private void permitSwaggerUri(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/docs/**")).permitAll());
    }
}
