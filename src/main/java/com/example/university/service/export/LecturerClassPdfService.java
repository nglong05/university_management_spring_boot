package com.example.university.service.export;

import com.example.university.dto.ClassTranscriptItemDTO;
import com.example.university.export.ClassTranscriptPdfExporter;
import com.example.university.entity.Lecturer;
import com.example.university.repository.LecturerJdbcRepository;
import com.example.university.service.exception.ForbiddenException;
import com.example.university.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LecturerClassPdfService {

    private final LecturerJdbcRepository lecturerRepo;
    private static final Pattern SEMESTER_PATTERN = Pattern.compile("^\\d{4}-[12]$");

    @Transactional(readOnly = true)
    public void exportClassPdf(String lecturerId,
                               String courseId,
                               String semesterId,
                               String lecturerLabel,
                               OutputStream out) {

        String maGv = normRequired(lecturerId, "Mã giảng viên");
        String maMon = normRequired(courseId, "Mã môn").toUpperCase();
        String maKy  = normSemester(semesterId);

        // kiểm tra quyền: GV có được phân công môn/kỳ này không
        if (!lecturerRepo.canGrade(maGv, maMon, maKy)) {
            throw new ForbiddenException("Giảng viên không được phân công môn/kỳ này.");
        }

        List<ClassTranscriptItemDTO> rows = lecturerRepo.classTranscript(maMon, maKy);

        try {
            ClassTranscriptPdfExporter exporter = new ClassTranscriptPdfExporter();
            String courseName = rows.isEmpty() ? "" : rows.get(0).courseName();
            String lecturerName = lecturerRepo.findById(maGv)
                    .map(Lecturer::getFullName)
                    .filter(name -> !name.isBlank())
                    .orElse(lecturerLabel == null || lecturerLabel.isBlank() ? maGv : lecturerLabel);

            exporter.export(maMon, courseName, maKy, lecturerName, rows, out);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất PDF lớp học", e);
        }
    }

    private static String normRequired(String s, String label) {
        if (s == null || s.isBlank()) {
            throw new ValidationException(label + " không được để trống");
        }
        return s.trim();
    }

    private static String normSemester(String semester) {
        if (semester == null || semester.isBlank()) {
            throw new ValidationException("Mã kỳ học không được để trống");
        }
        String s = semester.trim();
        if (!SEMESTER_PATTERN.matcher(s).matches()) {
            throw new ValidationException("Mã kỳ học không hợp lệ. Ví dụ: 2024-1, 2024-2.");
        }
        return s;
    }
}
