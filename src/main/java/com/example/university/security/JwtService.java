package com.example.university.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    private final Key key;
    private final long expMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.exp-minutes:120}") long expMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMinutes = expMinutes;
    }

    public String generate(AuthUser user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        if (user.getStudentId() != null)
            claims.put("maSv", user.getStudentId());
        if (user.getLecturerId() != null)
            claims.put("maGv", user.getLecturerId());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthUser parse(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        Claims c = jws.getBody();
        String username = c.getSubject();
        String role = (String) c.get("role");
        String maSv = (String) c.get("maSv");
        String maGv = (String) c.get("maGv");
        return new AuthUser(username, role, maSv, maGv);
    }
}
