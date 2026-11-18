package com.company.fooddash.config;

import com.company.fooddash.model.AppUser;
import com.company.fooddash.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final String email, password, name;

    public AdminBootstrap(AppUserRepository u, PasswordEncoder e, @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.name:FoodDash Admin}") String name) {
        users = u;
        encoder = e;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public void run(String... args) {
        if (email.isBlank() || password.length() < 12)
            return;
        if (!users.existsByEmailIgnoreCase(email))
            users.save(new AppUser(null, name, email.toLowerCase(), encoder.encode(password), Set.of("ADMIN"), true,
                    LocalDateTime.now()));
    }
}
