package com.example.university.ui;

import com.example.university.dto.UpdateGradeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Gọi API /api/lecturers/me/grades để ghi điểm từ UI.
 */
@Service
@RequiredArgsConstructor
public class UiLecturerService {

    private final RestTemplate restTemplate;

    private HttpHeaders authHeaders(UiSession s) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(s.getJwt());
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public void updateGrade(UiSession s, UpdateGradeRequest req) {
        HttpEntity<UpdateGradeRequest> entity = new HttpEntity<>(req, authHeaders(s));
        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/lecturers/me/grades",
                HttpMethod.PUT,
                entity,
                String.class
        );
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Không ghi được điểm");
        }
    }
}
