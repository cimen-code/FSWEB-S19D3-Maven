package com.workintech.s19d2.repository;

import com.workintech.s19d2.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByAuthority(String authority);
}
