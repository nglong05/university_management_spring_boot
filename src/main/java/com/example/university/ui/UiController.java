package com.example.university.ui;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.dto.UpdateGradeRequest;
import com.example.university.entity.Student;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * UI server-side:
 *   /ui/login           - form đăng nhập
 *   /ui/student/home    - trang sinh viên
 *   /ui/lecturer/home   - trang giảng viên
 */
@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UiController {
    private final RestTemplate restTemplate;

    public static final String SESSION_KEY = "UI_SESSION";

    private final UiAuthService authService;
    private final UiStudentService studentService;
    private final UiLecturerService lecturerService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginError", null);
        return "ui/login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session
    ) {
        try {
            UiSession uiSession = authService.login(username, password);
            session.setAttribute(SESSION_KEY, uiSession);
            if (uiSession.isStudent()) {
                return "redirect:/ui/student/home";
            } else if (uiSession.isLecturer()) {
                return "redirect:/ui/lecturer/home";
            } else if (uiSession.isAdmin()) {
                return "redirect:/ui/admin/home"; // có thể làm sau
            } else {
                model.addAttribute("loginError", "Vai trò không được hỗ trợ.");
                return "ui/login";
            }
        } catch (Exception e) {
            model.addAttribute("loginError", "Đăng nhập thất bại. Kiểm tra lại username/password.");
            return "ui/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/ui/login";
    }

    // ================== STUDENT UI ==================

    @GetMapping("/student/home")
    public String studentHome(@RequestParam(required = false) String semester,
                              HttpSession session,
                              Model model) {
        UiSession ui = requireLogin(session);
        if (!ui.isStudent()) {
            return "redirect:/ui/login";
        }

        try {
            Student profile = studentService.getMyProfile(ui);
            List<TranscriptItemDTO> transcript = studentService.getMyTranscript(ui, semester);
            GpaDTO gpa = studentService.getMyGpa(ui, semester);

            model.addAttribute("session", ui);
            model.addAttribute("profile", profile);
            model.addAttribute("transcript", transcript);
            model.addAttribute("gpa", gpa);
            model.addAttribute("semesterFilter", semester == null ? "" : semester);

            return "ui/student-home";
        } catch (Exception e) {
            model.addAttribute("error", "Không tải được dữ liệu sinh viên: " + e.getMessage());
            return "ui/student-home";
        }
    }

    // ================== LECTURER UI ==================

    @GetMapping("/lecturer/home")
    public String lecturerHome(HttpSession session, Model model) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) {
            return "redirect:/ui/login";
        }
        model.addAttribute("session", ui);
        model.addAttribute("gradeError", null);
        model.addAttribute("gradeOk", null);
        return "ui/lecturer-home";
    }

    @PostMapping("/lecturer/grade")
    public String submitGrade(
            @RequestParam String maSv,
            @RequestParam String maMon,
            @RequestParam String maKy,
            @RequestParam double diemQt,
            @RequestParam double diemGk,
            @RequestParam double diemCk,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) {
            return "redirect:/ui/login";
        }

        UpdateGradeRequest req = new UpdateGradeRequest();
        req.setMaSv(maSv);
        req.setMaMon(maMon);
        req.setMaKy(maKy);
        req.setDiemQt(java.math.BigDecimal.valueOf(diemQt));
        req.setDiemGk(java.math.BigDecimal.valueOf(diemGk));
        req.setDiemCk(java.math.BigDecimal.valueOf(diemCk));

        try {
            lecturerService.updateGrade(ui, req);
            model.addAttribute("gradeOk", "Đã lưu điểm thành công.");
            model.addAttribute("gradeError", null);
        } catch (Exception e) {
            model.addAttribute("gradeError", "Không thể lưu điểm: " + e.getMessage());
            model.addAttribute("gradeOk", null);
        }
        model.addAttribute("session", ui);
        return "ui/lecturer-home";
    }

    // ================== EXPORT ==================

    @GetMapping("/student/export-pdf")
    public ResponseEntity<byte[]> exportPdfFromUi(
            @RequestParam(value = "semester", required = false) String semester,
            HttpSession session) {

        UiSession ui = requireLogin(session);
        if (!ui.isStudent()) return ResponseEntity.status(403).build();

        String url = "/api/students/me/transcript.pdf";
        if (semester != null && !semester.isBlank()) {
            url += "?semester=" + org.springframework.web.util.UriUtils.encode(semester, java.nio.charset.StandardCharsets.UTF_8);
        }

        org.springframework.http.HttpHeaders h = new org.springframework.http.HttpHeaders();
        h.setBearerAuth(ui.getJwt());

        org.springframework.http.ResponseEntity<byte[]> resp = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(h),
                byte[].class
        );

        String filename = "transcript-" + ui.getStudentId()
                + (semester == null || semester.isBlank() ? "-all" : "-" + semester) + ".pdf";

        org.springframework.http.HttpHeaders out = new org.springframework.http.HttpHeaders();
        out.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        out.setContentDisposition(org.springframework.http.ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(resp.getBody(), out, org.springframework.http.HttpStatus.OK);
    }

    // ================== helper ==================

    private UiSession requireLogin(HttpSession session) {
        UiSession s = (UiSession) session.getAttribute(SESSION_KEY);
        if (s == null || !s.isLoggedIn()) {
            throw new IllegalStateException("Chưa đăng nhập ở UI.");
        }
        return s;
    }
}
