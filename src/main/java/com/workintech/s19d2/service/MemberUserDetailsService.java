package com.workintech.s19d2.service;

import com.workintech.s19d2.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService
        implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        return memberRepository.findByEmail(username)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "Member not found"
                )
            );
    }
}
