package com.example.university.service;

import com.example.university.dto.*;
import com.example.university.repository.LecturerJdbcRepository;
import com.example.university.repository.StudentJdbcRepository;
import com.example.university.service.exception.ForbiddenException;
import com.example.university.service.exception.NotFoundException;
import com.example.university.service.exception.ValidationException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service nghiệp vụ cho Giảng viên:
 * - Kiểm tra quyền chấm (đã được phân công môn/kỳ?)
 * - Validate dữ liệu điểm
 * - Gọi repository để upsert điểm
 *
 * Quy ước:
 * - Điểm nằm [0..10], làm tròn 2 chữ số thập phân để khớp DECIMAL(4,2) ở DB.
 * - Mã kỳ học theo pattern ^\\d{4}-[12]$ (ví dụ 2024-1).
 */
@Service
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerJdbcRepository lecturerRepo;
    private final StudentJdbcRepository studentRepo;

    private static final Pattern SEMESTER_PATTERN = Pattern.compile("^\\d{4}-[12]$");

    @Transactional
    public void updateGrade(String lecturerId, UpdateGradeRequest req) throws SQLException {
        String gvId = normRequired(lecturerId, "Mã giảng viên");

        // Chuẩn hóa + validate đầu vào
        sanitizeAndValidate(req);

        // 1) Kiểm tra sinh viên tồn tại
        if (studentRepo.findById(req.getMaSv()).isEmpty()) {
            throw new NotFoundException("Không tìm thấy sinh viên: " + req.getMaSv());
        }

        // 2) Kiểm tra quyền: giảng viên có được phân công dạy môn này ở kỳ này không?
        boolean allowed = lecturerRepo.canGrade(gvId, req.getMaMon(), req.getMaKy());
        if (!allowed) {
            throw new ForbiddenException("Giảng viên không được phân công môn/kỳ này.");
        }

        // 3) Ghi điểm (ON DUPLICATE KEY UPDATE) – cột diem_tong_ket trong DB sẽ tự tính
        int affected = lecturerRepo.upsertGrade(req);
        if (affected <= 0) {
            throw new ValidationException("Không thể lưu điểm. Vui lòng kiểm tra dữ liệu đầu vào.");
        }
    }

    // list transcript

    @Transactional(readOnly = true)
    public List<LecturerCourseDTO> listMyCourses(String lecturerId) {
        String gvId = normRequired(lecturerId, "Mã giảng viên");
        return lecturerRepo.listMyCourses(gvId);
    }

    @Transactional(readOnly = true)
    public List<ClassTranscriptItemDTO> getClassTranscript(
            String lecturerId,
            String courseId,
            String semesterId
    ) {
        String gvId = normRequired(lecturerId, "Mã giảng viên");
        String maMon = normRequired(courseId, "Mã môn").toUpperCase();
        String maKy  = normSemester(semesterId);

        // Chỉ xem được lớp nếu thực sự được phân công
        if (!lecturerRepo.canGrade(gvId, maMon, maKy)) {
            throw new ForbiddenException("Giảng viên không được phân công môn/kỳ này.");
        }
        return lecturerRepo.classTranscript(maMon, maKy);
    }

    // --------------------- PRIVATE HELPERS ---------------------

    private static String normRequired(String s, String field) {
        if (s == null || s.isBlank()) {
            throw new ValidationException("Thiếu: " + field);
        }
        return s.trim();
    }

    private static String normSemester(String semester) {
        String s = normRequired(semester, "Mã kỳ học");
        if (!SEMESTER_PATTERN.matcher(s).matches()) {
            throw new ValidationException("Mã kỳ học không hợp lệ. Ví dụ hợp lệ: 2024-1, 2024-2.");
        }
        return s;
    }

    /** Đưa điểm về scale(2) và kiểm tra khoảng [0..10]. */
    private static BigDecimal normScore(BigDecimal d, String label) {
        if (d == null) throw new ValidationException("Thiếu điểm: " + label);
        BigDecimal x = d.setScale(2, RoundingMode.HALF_UP);
        if (x.compareTo(BigDecimal.ZERO) < 0 || x.compareTo(BigDecimal.TEN) > 0) {
            throw new ValidationException(label + " phải nằm trong khoảng 0..10");
        }
        return x;
    }

    /** Chuẩn hóa + kiểm tra tất cả thuộc tính trong request. */
    private static void sanitizeAndValidate(UpdateGradeRequest req) {
        req.setMaSv(normRequired(req.getMaSv(), "Mã sinh viên").toUpperCase());
        req.setMaMon(normRequired(req.getMaMon(), "Mã môn").toUpperCase());
        req.setMaKy(normSemester(req.getMaKy()));

        req.setDiemQt(normScore(req.getDiemQt(), "Điểm quá trình"));
        req.setDiemGk(normScore(req.getDiemGk(), "Điểm giữa kỳ"));
        req.setDiemCk(normScore(req.getDiemCk(), "Điểm cuối kỳ"));
    }
    /**
     * Giảng viên xem danh sách đề tài NCKH của sinh viên đăng ký với mình.
     * Có thể filter theo semester (ma_ky) và status (trang_thai).
     */
    @Transactional
    public List<ResearchProjectDTO> listMyResearchProjects(
            String lecturerId,
            @Nullable String semester
    ) {
        String gvId = normRequired(lecturerId, "Mã giảng viên");

        String sem = null;
        if (semester != null && !semester.isBlank()) {
            sem = normSemester(semester);
        }


        return lecturerRepo.listResearchByLecturer(gvId, sem);
    }

    @Transactional
    public void reviewResearch(String lecturerId, UpdateResearchReviewRequest req) {
        String gvId = normRequired(lecturerId, "Mã giảng viên");

        req.setMaSv(normRequired(req.getMaSv(), "Mã sinh viên").toUpperCase());
        req.setMaKy(normSemester(req.getMaKy()));
        req.setTrangThai(normRequired(req.getTrangThai(), "Trạng thái đề tài"));
        if (req.getKetQua() != null) {
            req.setKetQua(req.getKetQua().trim());
        }

        int affected = lecturerRepo.updateResearchReview(gvId, req);
        if (affected == 0) {
            throw new NotFoundException("Không tìm thấy đề tài tương ứng hoặc không thuộc quyền của giảng viên.");
        }
    }

    /**
     * Giảng viên cập nhật trạng thái & nhận xét/ketQua cho 1 đề tài NCKH.
     */
    @Transactional
    public void updateResearchStatus(String lecturerId, UpdateResearchStatusRequest req) {
        String gvId = normRequired(lecturerId, "Mã giảng viên");
        if (req == null) {
            throw new ValidationException("Request không được null");
        }

        String maSv = normRequired(req.getMaSv(), "Mã sinh viên");
        String maKy = normRequired(req.getMaKy(), "Mã kỳ học");
        maKy = normSemester(maKy);

        String status = normRequired(req.getTrangThai(), "Trạng thái đề tài");
        if (status.length() > 100) {
            throw new ValidationException("Trạng thái quá dài (tối đa 100 ký tự)");
        }

        String ketQua = req.getKetQua();
        if (ketQua != null) {
            ketQua = ketQua.trim();
            if (ketQua.isEmpty()) {
                ketQua = null;
            } else if (ketQua.length() > 100) { // khớp với VARCHAR(100) của cột ket_qua
                throw new ValidationException("Nhận xét/kết quả quá dài (tối đa 100 ký tự)");
            }
        }

        int affected = lecturerRepo.updateResearchStatus(gvId, maSv, maKy, status, ketQua);
        if (affected == 0) {
            // Hoặc là không có đề tài đó, hoặc không phải đề tài của giảng viên này
            throw new NotFoundException("Không tìm thấy đề tài nghiên cứu của sinh viên/kỳ này hoặc bạn không phải người hướng dẫn.");
        }
    }
    private static String normSemesterAllowNull(@Nullable String semester) {
        if (semester == null || semester.isBlank()) return null;
        return normSemester(semester);
    }

}
