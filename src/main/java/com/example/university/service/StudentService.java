package com.example.university.service;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.example.university.repository.StudentJdbcRepository;
import com.example.university.service.exception.NotFoundException;
import com.example.university.service.exception.ValidationException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;
/**
 * Service cho sinh viên:
 * - Truy xuất hồ sơ
 * - Lấy bảng điểm theo kỳ / toàn khóa
 * - Tính GPA (đọc từ view table)
 */
@Service
@RequiredArgsConstructor
public class StudentService{

    private final StudentJdbcRepository studentRepo;
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("^\\d{4}-[12]$");

    // --------------------- PUBLIC API ---------------------

    @Transactional(readOnly = true)
    public Optional<Student> getProfile(String studentId) throws SQLException {
        String sid = normId(studentId);
        return studentRepo.findById(sid);
    }

    /**
     * Bảng điểm của sinh viên theo kỳ (null = toàn bộ).
     * Ném NotFound nếu sinh viên không tồn tại.
     */
    @Transactional(readOnly = true)
    public List<TranscriptItemDTO> getTranscript(String studentId, @Nullable String semesterId) throws SQLException {
        String sid = normId(studentId);
        ensureStudentExists(sid);
        String sem = normSemester(semesterId);
        return studentRepo.transcript(sid, sem);
    }

    /**
     * GPA (10 và 4). Nếu sinh viên không tồn tại → Optional.empty()
     * Nếu chưa có môn nào → trả GPA=0 (được chuẩn hóa tại đây để controller cứ trả 200 OK).
     */
    @Transactional(readOnly = true)
    public Optional<GpaDTO> getGpa(String studentId, @Nullable String semesterId) throws SQLException {
        String sid = normId(studentId);
        // Nếu SV không tồn tại -> Optional.empty() để Controller trả 404 (theo thói quen đồ án)
        if (studentRepo.findById(sid).isEmpty()) return Optional.empty();

        String sem = normSemester(semesterId);
        Optional<GpaDTO> db = studentRepo.gpa(sid, sem);
        // Nếu chưa có dữ liệu điểm: trả GPA 0 thay vì empty
        return Optional.of(db.orElse(new GpaDTO(sid, sem, 0.0, 0.0)));
    }

    // --------------------- PRIVATE HELPERS ---------------------

    private static String normId(String id) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("Thiếu mã định danh.");
        }
        return id.trim();
    }

    /** Hợp lệ hoặc null. */
    private static String normSemester(@Nullable String semester) {
        if (semester == null || semester.isBlank()) return null;
        String s = semester.trim();
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
}
