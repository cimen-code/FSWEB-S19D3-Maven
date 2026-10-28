package com.workintech.s19d2.repository;

import com.workintech.s19d2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository
        extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
