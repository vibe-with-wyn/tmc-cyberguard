package com.tmc.system.tmc_secure_system.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tmc.system.tmc_secure_system.entity.User;
import com.tmc.system.tmc_secure_system.entity.enums.RoleName;
import com.tmc.system.tmc_secure_system.entity.enums.UserStatus;
import com.tmc.system.tmc_secure_system.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            createIfMissing(users, encoder, "admin", "admin@tmc.com", "Admin@123", RoleName.ADMIN);
            createIfMissing(users, encoder, "operations", "operationtech@tmc.com", "OT@12345", RoleName.OT_OPERATOR);
            createIfMissing(users, encoder, "analyst", "analyst@tmc.com", "Analyst@123", RoleName.IT_ANALYST);
            createIfMissing(users, encoder, "compliance", "compliance@tmc.com", "Compliance@123", RoleName.COMPLIANCE_OFFICER);
            createIfMissing(users, encoder, "ciso", "ciso@tmc.com", "Ciso@12345", RoleName.CISO);
        };
    }

    private void createIfMissing(UserRepository users, PasswordEncoder enc,
                                 String username, String email, String rawPassword, RoleName role) {
        if (users.existsByUsernameIgnoreCase(username) || users.existsByEmailIgnoreCase(email)) return;

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(enc.encode(rawPassword));
        u.setRole(role);
        u.setStatus(UserStatus.ACTIVE);
        users.save(u);
    }
}
