package com.company.fooddash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt) throws Exception {
        return http.csrf(c -> c.disable()).cors(c -> {
        }).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 401 = "not signed in / token expired"  -> the frontend refreshes the token and retries.
                // 403 = "signed in, but not allowed"     -> the frontend just shows a message.
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            boolean expired = Boolean.TRUE.equals(req.getAttribute(JwtAuthFilter.EXPIRED_ATTR));
                            json(res, 401, expired ? "TOKEN_EXPIRED" : "UNAUTHENTICATED",
                                    expired ? "Your session has expired" : "Please sign in to continue");
                        })
                        .accessDeniedHandler((req, res, ex) -> json(res, 403, "FORBIDDEN",
                                "You do not have permission to do this")))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/auth/**", "/error", "/css/**", "/js/**", "/images/**", "/assets/**",
                                "/favicon.ico", "/favicon.svg", "/", "/index.html", "/login.html",
                                // React routes: Spring only serves index.html, React Router does the rest
                                "/explore", "/menu", "/story", "/login", "/register", "/orders")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
                        // Orders now belong to a signed-in customer
                        .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/*").authenticated() // /mine and /{id}
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/menu/**", "/api/menu").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/menu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/menu/**").hasRole("ADMIN")
                        .requestMatchers("/api/images/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("ADMIN") // list-all + status change
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class).build();
    }

    private static void json(jakarta.servlet.http.HttpServletResponse res, int status, String code, String message)
            throws java.io.IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"status\":" + status + ",\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        c.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/api/**", c);
        return s;
    }
}
