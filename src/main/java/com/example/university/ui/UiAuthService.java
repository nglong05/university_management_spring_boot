package com.example.university.ui;

import com.example.university.dto.AuthRequest;
import com.example.university.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service cho UI: gọi API /api/auth/login để lấy JWT.
 */
@Service
@RequiredArgsConstructor
public class UiAuthService {

    private final RestTemplate restTemplate;

    public UiSession login(String username, String password) {
        AuthRequest req = new AuthRequest();
        req.setUsername(username);
        req.setPassword(password);

        ResponseEntity<AuthResponse> resp =
                restTemplate.postForEntity("/api/auth/login", req, AuthResponse.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("Đăng nhập thất bại");
        }

        AuthResponse body = resp.getBody();
        UiSession session = new UiSession();
        session.setUsername(body.getUsername());
        session.setRole(body.getRole());
        session.setStudentId(body.getStudentId());
        session.setLecturerId(body.getLecturerId());
        session.setJwt(body.getAccessToken());
        return session;
    }
}
