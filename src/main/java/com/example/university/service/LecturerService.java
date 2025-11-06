package com.example.university.service;

import com.example.university.dto.UpdateGradeRequest;
import com.example.university.repository.LecturerJdbcRepository;
import com.example.university.repository.StudentJdbcRepository;
import com.example.university.service.exception.ForbiddenException;
import com.example.university.service.exception.NotFoundException;
import com.example.university.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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
}
