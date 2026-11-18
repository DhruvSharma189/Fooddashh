
package com.company.fooddash.config;

import com.company.fooddash.model.AppUser;
import com.company.fooddash.repository.AppUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner createInitialAdmin(
            AppUserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email:}") String adminEmail,
            @Value("${app.bootstrap-admin.password:}") String adminPassword,
            @Value("${app.bootstrap-admin.name:FoodDash Admin}") String adminName
    ) {
        return args -> {

            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                log.warn("Admin bootstrap skipped. Configure FOODDASH_ADMIN_EMAIL and FOODDASH_ADMIN_PASSWORD.");
                return;
            }

            if (adminPassword.length() < 12) {
                throw new IllegalStateException(
                        "Admin password must contain at least 12 characters."
                );
            }

            String email = adminEmail.trim().toLowerCase(Locale.ROOT);

            if (users.existsByEmailIgnoreCase(email)) {
                log.info("Admin account already exists: {}", email);
                return;
            }

            AppUser admin = new AppUser(
                    null,
                    adminName.trim(),
                    email,
                    passwordEncoder.encode(adminPassword),
                    Set.of("ADMIN"),
                    true,
                    LocalDateTime.now()
            );

            users.save(admin);

            log.info("Initial admin account created successfully: {}", email);
        };
    }
}
