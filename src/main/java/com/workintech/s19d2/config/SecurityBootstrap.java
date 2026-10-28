package com.workintech.s19d2.config;

import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SecurityBootstrap implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${APP_ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        Role userRole = findOrCreateRole("USER");
        Role adminRole = findOrCreateRole("ADMIN");

        if (adminEmail.isBlank() && adminPassword.isBlank()) {
            return;
        }

        if (adminEmail.isBlank() || adminPassword.length() < 12) {
            throw new IllegalStateException(
                "Admin bootstrap requires email and a password of at least 12 characters"
            );
        }

        String normalizedEmail =
            adminEmail.trim().toLowerCase(Locale.ROOT);

        if (memberRepository
                .findByEmail(normalizedEmail)
                .isPresent()) {
            return;
        }

        Member admin = new Member();
        admin.setEmail(normalizedEmail);
        admin.setPassword(
            passwordEncoder.encode(adminPassword)
        );

        admin.setRoles(List.of(userRole, adminRole));
        memberRepository.save(admin);
    }

    private Role findOrCreateRole(String authority) {

        return roleRepository.findByAuthority(authority)
            .orElseGet(() -> {
                Role role = new Role();
                role.setAuthority(authority);
                return roleRepository.save(role);
            });
    }
}
