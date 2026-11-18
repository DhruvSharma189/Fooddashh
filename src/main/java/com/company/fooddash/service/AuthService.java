
package com.company.fooddash.service;

import com.company.fooddash.dto.AuthRequest;
import com.company.fooddash.dto.AuthResponse;
import com.company.fooddash.dto.RegisterRequest;
import com.company.fooddash.model.AppUser;
import com.company.fooddash.repository.AppUserRepository;
import com.company.fooddash.model.RefreshToken;
import com.company.fooddash.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.company.fooddash.config.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuditService audit;
    private final long expiry;
    private final RefreshTokenRepository refreshTokens;
    private final long refreshDays;
    private static final SecureRandom RANDOM = new SecureRandom();

    public AuthService(
            AppUserRepository users,
            PasswordEncoder encoder,
            JwtService jwt,
            AuditService audit,
            @Value("${app.jwt.expiration-ms:900000}") long expiry,
            RefreshTokenRepository refreshTokens,
            @Value("${app.jwt.refresh-expiration-days:7}") long refreshDays
    ) {
        this.refreshTokens = refreshTokens;
        this.refreshDays = refreshDays;
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.audit = audit;
        this.expiry = expiry;
    }

    public AuthResponse register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        AppUser user = new AppUser(
                null,
                request.name().trim(),
                email,
                encoder.encode(request.password()),
                Set.of("CUSTOMER"),
                true,
                LocalDateTime.now()
        );

        AppUser savedUser = users.save(user);

        audit.record(
                email,
                "CUSTOMER_REGISTERED",
                "Customer account created"
        );

        return createAuthResponse(savedUser);
    }

    public AuthResponse login(AuthRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        AppUser user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!user.enabled()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        if (!encoder.matches(request.password(), user.passwordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        audit.record(
                user.email(),
                "LOGIN",
                "Successful login"
        );

        return createAuthResponse(user);
    }

    public AuthResponse refresh(String rawRefreshToken) {
        refreshTokens.deleteByExpiresAtBefore(Instant.now());
        RefreshToken stored = refreshTokens.findByTokenHash(hash(rawRefreshToken))
                .filter(t -> t.expiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Session expired. Please sign in again."));
        // Rotation: a refresh token can be used only once.
        refreshTokens.deleteByTokenHash(stored.tokenHash());
        AppUser user = users.findByEmailIgnoreCase(stored.email())
                .filter(AppUser::enabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Session expired. Please sign in again."));
        return createAuthResponse(user);
    }

    public void logout(String rawRefreshToken) {
        refreshTokens.deleteByTokenHash(hash(rawRefreshToken));
    }

    private AuthResponse createAuthResponse(AppUser user) {

        String token = jwt.issue(
                user.email(),
                user.roles()
                        .stream()
                        .map(role -> "ROLE_" + role)
                        .toList()
        );

        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokens.save(new RefreshToken(null, hash(refresh), user.email(),
                Instant.now().plusSeconds(refreshDays * 86400)));

        return new AuthResponse(
                token,
                refresh,
                "Bearer",
                expiry / 1000,
                user.name(),
                user.email(),
                user.roles()
        );
    }

    private static String hash(String raw) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(d);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
