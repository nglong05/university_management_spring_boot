package com.example.university.ui;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.example.university.dto.ResearchProjectDTO;
import com.example.university.dto.ResearchRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Gọi các API /api/students/me/** để lấy dữ liệu cho UI.
 */
@Service
@RequiredArgsConstructor
public class UiStudentService {

    private final RestTemplate restTemplate;

    private HttpHeaders authHeaders(UiSession s) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(s.getJwt());
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }

    public Student getMyProfile(UiSession s) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));
        ResponseEntity<Student> resp = restTemplate.exchange(
                "/api/students/me",
                HttpMethod.GET,
                entity,
                Student.class
        );
        return resp.getBody();
    }

    public List<TranscriptItemDTO> getMyTranscript(UiSession s, String semester) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));
        String url = "/api/students/me/transcript";
        if (semester != null && !semester.isBlank()) {
            url += "?semester=" + semester;
        }

        ResponseEntity<List<TranscriptItemDTO>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<TranscriptItemDTO>>() {}
        );
        return resp.getBody();
    }

    public GpaDTO getMyGpa(UiSession s, String semester) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));
        String url = "/api/students/me/gpa";
        if (semester != null && !semester.isBlank()) {
            url += "?semester=" + semester;
        }
        ResponseEntity<GpaDTO> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GpaDTO.class
        );
        return resp.getBody();
    }

    public void registerResearch(UiSession s, ResearchRegistrationRequest req) {
        HttpHeaders headers = authHeaders(s);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ResearchRegistrationRequest> entity = new HttpEntity<>(req, headers);
        ResponseEntity<Void> resp = restTemplate.exchange(
                "/api/students/me/research-projects",
                HttpMethod.POST,
                entity,
                Void.class
        );
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Không đăng ký được đề tài");
        }
    }

    public List<ResearchProjectDTO> getMyResearch(UiSession s, String semester) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));
        String url = "/api/students/me/research-projects";
        if (semester != null && !semester.isBlank()) {
            url += "?semester=" + semester;
        }

        ResponseEntity<List<ResearchProjectDTO>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ResearchProjectDTO>>() {}
        );
        return resp.getBody();
    }

}
