package com.workintech.s19d2.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        String[] accountPaths = {
            "/account",
            "/account/**",
            "/accounts",
            "/accounts/**"
        };

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/auth/register"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    accountPaths
                ).hasAnyAuthority("USER", "ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    accountPaths
                ).hasAuthority("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    accountPaths
                ).hasAuthority("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    accountPaths
                ).hasAuthority("ADMIN")

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
