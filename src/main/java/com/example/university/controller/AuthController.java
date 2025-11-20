package com.example.university.controller;

import com.example.university.dto.AuthRequest;
import com.example.university.dto.AuthResponse;
import com.example.university.entity.Account;
import com.example.university.repository.AccountJdbcRepository;
import com.example.university.security.AuthUser;
import com.example.university.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;
    private final AccountJdbcRepository accountRepo;
    private final JwtService jwtService;

    @Value("${app.security.jwt.exp-minutes:120}")
    private long expMinutes;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        // 1) Xác thực username/password qua AuthenticationManager (đọc từ DB)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        // 2) Nạp thêm thông tin liên kết SV/GV từ DB để nhúng vào token
        Account acc = accountRepo.findByUsername(req.getUsername()).orElseThrow();

        AuthUser au = new AuthUser(acc.getUsername(), acc.getRole(), acc.getStudentId(), acc.getTeacherId());
        String token;
        // got some funny bugs here
        try {
            System.out.println("au = " + au);
            token = jwtService.generate(au);
            System.out.println("jwtService = " + jwtService);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return ResponseEntity.ok(
                new AuthResponse("Bearer", token, Duration.ofMinutes(expMinutes).getSeconds(),
                        acc.getUsername(), acc.getRole(), acc.getStudentId(), acc.getTeacherId()));
    }

    /** Kiểm tra token và xem thông tin user hiện tại */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal Object principal) {
        if (principal instanceof AuthUser au) {
            return ResponseEntity.ok(new AuthResponse("Bearer","",0,
                    au.getUsername(), au.getRole(), au.getStudentId(), au.getLecturerId()));
        }
        return ResponseEntity.status(401).build();
    }
}
