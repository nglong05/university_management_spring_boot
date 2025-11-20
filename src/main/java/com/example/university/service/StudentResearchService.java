package com.example.university.service;

import com.example.university.dto.ResearchProjectDTO;
import com.example.university.dto.ResearchRegistrationRequest;
import com.example.university.repository.ResearchJdbcRepository;
import com.example.university.repository.StudentJdbcRepository;
import com.example.university.service.exception.NotFoundException;
import com.example.university.service.exception.ValidationException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service cho các chức năng:
 *  - Sinh viên xem danh sách đề tài NCKH của mình
 *  - Sinh viên đăng ký đề tài mới
 *  - (Tuỳ chọn) hủy đăng ký đề tài
 */
@Service
@RequiredArgsConstructor
public class StudentResearchService {

    private final StudentJdbcRepository studentRepo;
    private final ResearchJdbcRepository researchRepo;

    private static final Pattern SEMESTER_PATTERN = Pattern.compile("^\\d{4}-[12]$");

    // ============== PUBLIC API ==============

    @Transactional(readOnly = true)
    public List<ResearchProjectDTO> listMyProjects(String studentId,
                                                   @Nullable String semesterId) throws SQLException {
        String sid = normId(studentId);
        ensureStudentExists(sid);
        String sem = normSemester(semesterId);
        return researchRepo.findByStudent(sid, sem);
    }

    @Transactional
    public void register(String studentId,
                         ResearchRegistrationRequest req) throws SQLException {
        String sid = normId(studentId);
        ensureStudentExists(sid);
        sanitize(req);

        if (researchRepo.exists(sid, req.getMaGv(), req.getMaKy())) {
            throw new ValidationException("Bạn đã có đề tài với giảng viên và kỳ học này.");
        }

        int affected = researchRepo.insert(sid, req, "CHO_DUYET");
        if (affected <= 0) {
            throw new ValidationException("Không thể đăng ký đề tài. Vui lòng thử lại.");
        }
    }

    @Transactional
    public void cancel(String studentId,
                       String lecturerId,
                       String semesterId) throws SQLException {
        String sid = normId(studentId);
        ensureStudentExists(sid);
        String gv = normRequired(lecturerId, "Mã giảng viên").toUpperCase();
        String sem = normSemesterRequired(semesterId);

        int affected = researchRepo.updateStatus(sid, gv, sem, "HUY");
        if (affected == 0) {
            throw new NotFoundException("Không tìm thấy đề tài để hủy.");
        }
    }

    // ============== HELPERS ==============

    private static String normId(String id) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("Thiếu mã sinh viên.");
        }
        return id.trim();
    }

    private static String normRequired(String s, String field) {
        if (s == null || s.isBlank()) {
            throw new ValidationException("Thiếu: " + field);
        }
        return s.trim();
    }

    private static String normSemester(@Nullable String semester) {
        if (semester == null || semester.isBlank()) return null;
        String s = semester.trim();
        if (!SEMESTER_PATTERN.matcher(s).matches()) {
            throw new ValidationException("Mã kỳ học không hợp lệ. Ví dụ hợp lệ: 2024-1, 2024-2.");
        }
        return s;
    }

    private static String normSemesterRequired(String semester) {
        String s = normRequired(semester, "Mã kỳ học");
        if (!SEMESTER_PATTERN.matcher(s).matches()) {
            throw new ValidationException("Mã kỳ học không hợp lệ. Ví dụ hợp lệ: 2024-1, 2024-2.");
        }
        return s;
    }

    private void ensureStudentExists(String studentId) throws SQLException {
        if (studentRepo.findById(studentId).isEmpty()) {
            throw new NotFoundException("Không tìm thấy sinh viên: " + studentId);
        }
    }

    /** Chuẩn hoá request của sinh viên */
    private static void sanitize(ResearchRegistrationRequest req) {
        req.setMaGv(normRequired(req.getMaGv(), "Mã giảng viên").toUpperCase());
        req.setMaKy(normSemesterRequired(req.getMaKy()));
        req.setTenDeTai(normRequired(req.getTenDeTai(), "Tên đề tài"));
        req.setMoTa(normRequired(req.getMoTa(), "Mô tả"));

        if (req.getFileDinhKem() != null && req.getFileDinhKem().isBlank()) {
            req.setFileDinhKem(null);
        }
    }
}
