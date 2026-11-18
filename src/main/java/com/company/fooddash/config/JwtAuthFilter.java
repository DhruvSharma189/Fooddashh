package com.company.fooddash.config;

import com.company.fooddash.model.AppUser;
import com.company.fooddash.repository.AppUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    public static final String EXPIRED_ATTR = "jwt.expired";
    private final JwtService jwt;
    private final AppUserRepository users;
    public JwtAuthFilter(JwtService jwt, AppUserRepository users) { this.jwt = jwt; this.users = users; }

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwt.parse(header.substring(7));
                AppUser user = users.findByEmailIgnoreCase(claims.getSubject()).orElse(null);
                if (user != null && user.enabled()) {
                    var authorities = user.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
                    var authentication = new UsernamePasswordAuthenticationToken(user.email(), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else SecurityContextHolder.clearContext();
            } catch (ExpiredJwtException e) {
                req.setAttribute(EXPIRED_ATTR, true);
                SecurityContextHolder.clearContext();
            } catch (Exception ignored) { SecurityContextHolder.clearContext(); }
        }
        chain.doFilter(req, res);
    }
}
