package com.example.university.service.export;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.example.university.export.TranscriptPdfExporter;
import com.example.university.service.StudentService;
import com.example.university.service.exception.NotFoundException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final StudentService studentService;

    public byte[] transcriptPdf(String studentId, @Nullable String semester) throws SQLException {
        Student sv = studentService.getProfile(studentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên: " + studentId));

        List<TranscriptItemDTO> rows = studentService.getTranscript(studentId, semester);
        GpaDTO gpa = studentService.getGpa(studentId, semester)
                .orElse(new GpaDTO(studentId, semester, 0.0, 0.0));

        try {
            return TranscriptPdfExporter.build(sv, rows, gpa, semester);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Lỗi tạo PDF", e);
        }
    }
}
