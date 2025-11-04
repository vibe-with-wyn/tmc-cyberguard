package com.tmc.system.tmc_secure_system.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tmc.system.tmc_secure_system.entity.User;
import com.tmc.system.tmc_secure_system.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User u = users.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authority = new SimpleGrantedAuthority("ROLE_" + u.getRole().name());
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(List.of(authority))
                .accountLocked(u.getLockedUntil() != null && u.getLockedUntil().isAfter(java.time.LocalDateTime.now()))
                .disabled(u.getStatus() != null && !"ACTIVE".equals(u.getStatus().name()))
                .build();
    }
}
