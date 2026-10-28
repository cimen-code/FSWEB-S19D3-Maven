package com.workintech.s19d2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Profile("github-oauth")
public class GithubOAuthSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain githubOAuthFilterChain(
            HttpSecurity http,
            @Value("${APP_GITHUB_ALLOWED_IDS:}")
            String allowedIds) throws Exception {

        Set<String> allowedGithubIds =
            Arrays.stream(allowedIds.split(","))
                .map(String::trim)
                .filter(id -> !id.isBlank())
                .collect(Collectors.toUnmodifiableSet());

        http
            .securityMatcher(request -> {
                String path = request.getServletPath();

                boolean accountRead =
                    "GET".equals(request.getMethod()) &&
                    (
                        path.equals("/account") ||
                        path.startsWith("/account/") ||
                        path.equals("/accounts") ||
                        path.startsWith("/accounts/")
                    );

                boolean oauthEndpoint =
                    path.equals("/login") ||
                    path.startsWith("/login/") ||
                    path.startsWith("/oauth2/");

                return accountRead || oauthEndpoint;
            })
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/login/**",
                    "/oauth2/**"
                ).permitAll()

                .anyRequest()
                .hasAnyAuthority("USER", "ADMIN")
            )
            .httpBasic(Customizer.withDefaults())
            .oauth2Login(oauth ->
                oauth.userInfoEndpoint(userInfo ->
                    userInfo.userAuthoritiesMapper(authorities -> {

                        Set<GrantedAuthority> mapped =
                            new HashSet<>(authorities);

                        String githubId = authorities.stream()
                            .filter(OAuth2UserAuthority.class::isInstance)
                            .map(OAuth2UserAuthority.class::cast)
                            .map(a -> a.getAttributes().get("id"))
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .findFirst()
                            .orElse(null);

                        if (githubId != null &&
                            allowedGithubIds.contains(githubId)) {

                            mapped.add(
                                new SimpleGrantedAuthority("USER")
                            );
                        }

                        // GitHub OAuth never grants ADMIN.
                        return mapped;
                    })
                )
            );

        return http.build();
    }
}
