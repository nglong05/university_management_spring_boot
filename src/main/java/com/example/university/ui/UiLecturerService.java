package com.example.university.ui;

import com.example.university.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    public List<ClassTranscriptItemDTO> classTranscript(
            UiSession s,
            String courseId,
            String semesterId
    ) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));
        String url = String.format(
                "/api/lecturers/me/courses/%s/semesters/%s/transcript",
                courseId, semesterId
        );
        ResponseEntity<List<ClassTranscriptItemDTO>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ClassTranscriptItemDTO>>() {}
        );
        return resp.getBody();
    }

    public List<LecturerCourseDTO> myCourses(UiSession s) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));
        String url = "/api/lecturers/me/courses";

        ResponseEntity<List<LecturerCourseDTO>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<LecturerCourseDTO>>() {}
        );

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Không tải được danh sách môn");
        }

        return resp.getBody();
    }
    public byte[] downloadClassPdf(UiSession s, String maMon, String maKy) {
        HttpHeaders h = authHeaders(s);
        h.setAccept(List.of(MediaType.APPLICATION_PDF));
        HttpEntity<Void> entity = new HttpEntity<>(h);

        ResponseEntity<byte[]> resp = restTemplate.exchange(
                "/api/lecturers/me/classes/{courseId}/semesters/{semesterId}/pdf",
                HttpMethod.GET,
                entity,
                byte[].class,
                maMon,
                maKy
        );
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("Không tải được PDF lớp học");
        }
        return resp.getBody();
    }
    public List<ResearchProjectDTO> getMyResearchProjects(UiSession s, String semester) {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(s));

        String url = "/api/lecturers/me/research-projects";
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
    public void reviewResearch(UiSession s, UpdateResearchReviewRequest req) {
        HttpEntity<UpdateResearchReviewRequest> entity = new HttpEntity<>(req, authHeaders(s));

        ResponseEntity<String> resp = restTemplate.exchange(
                "/api/lecturers/me/research-projects/review",
                HttpMethod.PUT,
                entity,
                String.class
        );
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Không cập nhật được trạng thái đề tài");
        }
    }

}
