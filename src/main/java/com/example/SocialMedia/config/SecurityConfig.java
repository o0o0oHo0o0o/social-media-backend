package com.example.SocialMedia.config;

import com.example.SocialMedia.config.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsProperties.class)
public class SecurityConfig {
        private final JwtAuthFilter jwtAuthFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
        private final AuthenticationProvider authenticationProvider;

        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
        @Value("${spring.application.frontend}")
        private String frontendUrl;
        private final CorsProperties corsProperties;
        public static String[] PUBLIC_ENDPOINTS = {
                        "/auth/register",
                        "/auth/login",
                        "/verification/**",
                        "/oauth2/**",
                        "/api/public/**",
                        "/auth/logout",
                        "/auth/refresh",
                        "/health",
                        "/actuator/health",
                        // Swagger UI endpoints
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/api/posts/**",
                        "/api/profile/**",
                        "/api/feed/**",
                        "/api/reactions/**"
        };
        public static String[] ADMIN_ENDPOINTS = {
                        "/api/admin/**"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfiguration()))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                .requestMatchers("/ws/**", "/ws-sockjs/**").permitAll()
                                                .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oAuth2LoginSuccessHandler)
                                                .failureUrl(frontendUrl + "/login?error=oauth2_failed"))
                                .authenticationProvider(authenticationProvider)
                                .exceptionHandling(exceptionHandler -> exceptionHandler
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(jwtAccessDeniedHandler))
                                .headers(headers -> headers
                                                .contentSecurityPolicy(
                                                                csp -> csp.policyDirectives("default-src 'self'"))
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(new CorsCredentialsFilter(),
                                                UsernamePasswordAuthenticationFilter.class); // THÊM DÒNG NÀY
                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfiguration() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                List<String> allowedOrigins = new ArrayList<>(corsProperties.getAllowedOrigins());
                if (frontendUrl != null && !frontendUrl.isBlank() && !allowedOrigins.contains(frontendUrl)) {
                        allowedOrigins.add(frontendUrl);
                }

                corsConfiguration.setAllowedMethods(corsProperties.getAllowedMethods());
                corsConfiguration.setAllowedOriginPatterns(allowedOrigins);
                corsConfiguration.setAllowedOrigins(allowedOrigins);
                corsConfiguration.setAllowedHeaders(corsProperties.getAllowedHeaders());
                corsConfiguration.setExposedHeaders(corsProperties.getExposedHeaders());
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setMaxAge(3600L);

                corsConfiguration.addExposedHeader("Set-Cookie");

                UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
                urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
                return urlBasedCorsConfigurationSource;
        }

}
