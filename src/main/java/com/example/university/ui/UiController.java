package com.example.university.ui;

import com.example.university.dto.*;
import com.example.university.entity.Student;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
                return "redirect:/ui/student/profile";
            } else if (uiSession.isLecturer()) {
                return "redirect:/ui/lecturer/profile";
            } else if (uiSession.isAdmin()) {
                return "redirect:/ui/admin/home"; // tạm thời
            }
            else {
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
        // Ví dụ: coi "profile" là trang chính
        return "redirect:/ui/student/profile";
    }

    @PostMapping("/student/research/register")
    public String registerResearch(
            @RequestParam String maGv,
            @RequestParam String maKy,
            @RequestParam String tenDeTai,
            @RequestParam String moTa,
            @RequestParam(required = false) String fileDinhKem,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isStudent()) {
            return "redirect:/ui/login";
        }

        ResearchRegistrationRequest req = new ResearchRegistrationRequest();
        req.setMaGv(maGv);
        req.setMaKy(maKy);
        req.setTenDeTai(tenDeTai);
        req.setMoTa(moTa);
        req.setFileDinhKem(fileDinhKem);

        try {
            studentService.registerResearch(ui, req);
            model.addAttribute("researchOk", "Đã gửi đăng ký đề tài thành công.");
            model.addAttribute("researchError", null);
        } catch (Exception e) {
            model.addAttribute("researchError", "Không thể đăng ký đề tài: " + e.getMessage());
            model.addAttribute("researchOk", null);
        }

        model.addAttribute("session", ui);
        model.addAttribute("researchList", studentService.getMyResearch(ui, null));

        return "ui/student-research";
    }


    // ================== LECTURER UI ==================

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

        // Sau khi chấm xong: load lại danh sách môn + lớp tương ứng
        try {
            var courses = lecturerService.myCourses(ui);
            model.addAttribute("courses", courses);
        } catch (Exception e) {
            model.addAttribute("coursesError", "Không tải được danh sách môn: " + e.getMessage());
        }

        try {
            var classTranscript = lecturerService.classTranscript(ui, maMon, maKy);
            model.addAttribute("classTranscript", classTranscript);
            model.addAttribute("selectedCourseId", maMon);
            model.addAttribute("selectedSemesterId", maKy);
        } catch (Exception e) {
            model.addAttribute("classTranscriptError", "Không tải được danh sách sinh viên: " + e.getMessage());
        }

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
    @GetMapping("/lecturer/class-pdf")
    public void downloadClassPdf(
            @RequestParam String maMon,
            @RequestParam String maKy,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) {
            response.sendRedirect("/ui/login");
            return;
        }

        byte[] pdf = lecturerService.downloadClassPdf(ui, maMon, maKy);

        String fileName = URLEncoder.encode(
                "bang_diem_" + maMon + "_" + maKy + ".pdf",
                StandardCharsets.UTF_8
        );
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.getOutputStream().write(pdf);
    }
    @GetMapping("/lecturer/home")
    public String lecturerHome(
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String semesterId,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) {
            return "redirect:/ui/login";
        }

        model.addAttribute("session", ui);
        model.addAttribute("gradeError", null);
        model.addAttribute("gradeOk", null);

        // 1) Danh sách môn được phân công
        try {
            var courses = lecturerService.myCourses(ui);
            model.addAttribute("courses", courses);
        } catch (Exception e) {
            model.addAttribute("coursesError", "Không tải được danh sách môn: " + e.getMessage());
        }

        // 2) Bảng điểm lớp (nếu đã chọn môn + kỳ)
        if (courseId != null && !courseId.isBlank()
                && semesterId != null && !semesterId.isBlank()) {
            try {
                var classTranscript = lecturerService.classTranscript(ui, courseId, semesterId);
                model.addAttribute("classTranscript", classTranscript);
                model.addAttribute("selectedCourseId", courseId);
                model.addAttribute("selectedSemesterId", semesterId);
            } catch (Exception e) {
                model.addAttribute("classTranscriptError", "Không tải được danh sách sinh viên: " + e.getMessage());
            }
        }

        return "ui/lecturer-home";   // giờ chỉ là trang chấm điểm
    }



    @PostMapping("/lecturer/research-review")
    public String submitResearchReview(
            @RequestParam String maSv,
            @RequestParam String maKy,
            @RequestParam String trangThai,
            @RequestParam(required = false) String ketQua,
            @RequestParam(required = false, name = "semester") String filterSemester,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) {
            return "redirect:/ui/login";
        }

        UpdateResearchReviewRequest req = new UpdateResearchReviewRequest();
        req.setMaSv(maSv);
        req.setMaKy(maKy);
        req.setTrangThai(trangThai);
        req.setKetQua(ketQua);

        try {
            lecturerService.reviewResearch(ui, req);
            model.addAttribute("reviewOk", "Đã cập nhật đề tài.");
            model.addAttribute("reviewError", null);
        } catch (Exception e) {
            model.addAttribute("reviewError", "Không cập nhật được đề tài: " + e.getMessage());
            model.addAttribute("reviewOk", null);
        }

        model.addAttribute("session", ui);

        // load lại danh sách đề tài sau khi cập nhật
        try {
            java.util.List<ResearchProjectDTO> research = lecturerService.getMyResearchProjects(ui, filterSemester);
            model.addAttribute("researchList", research);
        } catch (Exception e) {
            model.addAttribute("researchError", "Không tải được danh sách đề tài: " + e.getMessage());
        }

        model.addAttribute("semesterFilter", filterSemester == null ? "" : filterSemester);
        return "ui/lecturer-research";
    }


    @GetMapping("/lecturer/research")
    public String lecturerResearch(
            @RequestParam(required = false, name = "semester") String researchSemester,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) {
            return "redirect:/ui/login";
        }

        model.addAttribute("session", ui);
        model.addAttribute("reviewOk", null);
        model.addAttribute("reviewError", null);

        try {
            var researchList = lecturerService.getMyResearchProjects(ui, researchSemester);
            model.addAttribute("researchList", researchList);
        } catch (Exception e) {
            model.addAttribute("researchError", "Không tải được danh sách đề tài NCKH: " + e.getMessage());
        }

        model.addAttribute("semesterFilter", researchSemester == null ? "" : researchSemester);
        return "ui/lecturer-research";
    }


    @GetMapping("/student/profile")
    public String studentProfile(HttpSession session, Model model) {
        UiSession ui = requireLogin(session);
        if (!ui.isStudent()) return "redirect:/ui/login";

        model.addAttribute("session", ui);
        model.addAttribute("profile", studentService.getMyProfile(ui));
        return "ui/student-profile";
    }

    @GetMapping("/student/grades")
    public String studentGrades(
            @RequestParam(required = false) String semester,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isStudent()) return "redirect:/ui/login";

        model.addAttribute("session", ui);
        model.addAttribute("semesterFilter", semester == null ? "" : semester);
        model.addAttribute("gpa", studentService.getMyGpa(ui, semester));
        model.addAttribute("transcript", studentService.getMyTranscript(ui, semester));

        return "ui/student-grades";
    }


    @GetMapping("/student/research")
    public String studentResearch(HttpSession session, Model model) {
        UiSession ui = requireLogin(session);
        if (!ui.isStudent()) return "redirect:/ui/login";

        model.addAttribute("session", ui);
        model.addAttribute("researchList", studentService.getMyResearch(ui, null));
        model.addAttribute("researchOk", null);
        model.addAttribute("researchError", null);

        return "ui/student-research";
    }

    @GetMapping("/lecturer/profile")
    public String lecturerProfile(HttpSession session, Model model) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) return "redirect:/ui/login";

        model.addAttribute("session", ui);
        model.addAttribute("profile", lecturerService.getMyProfile(ui));

        return "ui/lecturer-profile";
    }

    @GetMapping("/lecturer/courses")
    public String lecturerCourses(HttpSession session, Model model) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) return "redirect:/ui/login";

        model.addAttribute("session", ui);
        model.addAttribute("courses", lecturerService.myCourses(ui));

        return "ui/lecturer-courses";
    }
    @GetMapping("/lecturer/class")
    public String lecturerClass(
            @RequestParam String course,
            @RequestParam String semester,
            HttpSession session,
            Model model
    ) {
        UiSession ui = requireLogin(session);
        if (!ui.isLecturer()) return "redirect:/ui/login";

        model.addAttribute("session", ui);
        model.addAttribute("classTranscript", lecturerService.classTranscript(ui, course, semester));
        model.addAttribute("courseId", course);
        model.addAttribute("semesterId", semester);

        return "ui/lecturer-class";
    }

}
