package com.example.university.export;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TranscriptPdfExporter {

    private static String fmt(BigDecimal x) {
        return x == null ? "–" : x.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static byte[] build(Student sv, List<TranscriptItemDTO> rows, GpaDTO gpa, String semester) throws Exception {
        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Dùng font built-in của OpenPDF (không cần BaseFont)
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font strong    = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normal    = new Font(Font.HELVETICA, 11, Font.NORMAL);

        // Tiêu đề
        Paragraph title = new Paragraph("BANG DIEM SINH VIEN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        // Thông tin SV
        Paragraph p1 = new Paragraph();
        p1.setFont(normal);
        p1.add("Ma SV: "); p1.add(new Chunk(sv.getId() + "    ", strong));
        p1.add("Ho ten: "); p1.add(new Chunk(sv.getFullName() + "\n", strong));
        if (sv.getDateOfBirth() != null) {
            p1.add("Ngay sinh: "); p1.add(new Chunk(sv.getDateOfBirth().toString() + "    ", strong));
        }
        // Nếu Student có các field ngành/khoa chuẩn, sửa theo entity thật của bạn
        // (Ở bản bạn gửi, Student chỉ có departmentID, chưa có majorID)
        if (sv.getDepartmentID() != null) {
            p1.add("Khoa: "); p1.add(new Chunk(sv.getDepartmentID() + "\n", strong));
        }
        p1.add("Ky: "); p1.add(new Chunk(semester == null ? "Tich luy toan khoa" : semester, strong));
        doc.add(p1);
        doc.add(Chunk.NEWLINE);

        // GPA
        if (gpa != null) {
            Paragraph p2 = new Paragraph(
                    "GPA he 10: " + (gpa.gpa10() == null ? "0.00" : String.format("%.2f", gpa.gpa10()))
                            + "    GPA he 4: " + (gpa.gpa4() == null ? "0.00" : String.format("%.2f", gpa.gpa4())),
                    normal
            );
            doc.add(p2);
            doc.add(Chunk.NEWLINE);
        }

        // Bảng điểm
        PdfPTable table = new PdfPTable(new float[]{2.2f, 5.2f, 1.0f, 1.2f, 1.2f, 1.2f, 1.5f, 1.8f});
        table.setWidthPercentage(100);

        addHeader(table, "Ma mon", strong);
        addHeader(table, "Ten mon", strong);
        addHeader(table, "TC", strong);
        addHeader(table, "QT", strong);
        addHeader(table, "GK", strong);
        addHeader(table, "CK", strong);
        addHeader(table, "Tong ket", strong);
        addHeader(table, "Ky", strong);

        for (TranscriptItemDTO r : rows) {
            addCell(table, r.courseId(), normal, Element.ALIGN_LEFT);
            addCell(table, r.courseName(), normal, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(r.credits()), normal, Element.ALIGN_CENTER);
            addCell(table, fmt(r.processScore()), normal, Element.ALIGN_RIGHT);
            addCell(table, fmt(r.midtermScore()), normal, Element.ALIGN_RIGHT);
            addCell(table, fmt(r.finalExamScore()), normal, Element.ALIGN_RIGHT);
            addCell(table, fmt(r.finalScore()), normal, Element.ALIGN_RIGHT);
            addCell(table, r.semesterId(), normal, Element.ALIGN_CENTER);
        }

        doc.add(table);
        doc.add(Chunk.NEWLINE);

        Paragraph footer = new Paragraph("Ngay xuat: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normal);
        footer.setAlignment(Element.ALIGN_RIGHT);
        doc.add(footer);

        doc.close();
        return baos.toByteArray();
    }

    private static void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBackgroundColor(new Color(245, 247, 250));
        c.setPadding(6f);
        table.addCell(c);
    }

    private static void addCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text, font));
        c.setHorizontalAlignment(align);
        c.setPadding(5f);
        table.addCell(c);
    }
}
