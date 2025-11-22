package com.example.university.export;

import com.example.university.dto.ClassGradeDTO;
import com.example.university.dto.ClassTranscriptItemDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClassTranscriptPdfExporter {

    private final Font titleFont;
    private final Font headerFont;
    private final Font normalFont;

    public ClassTranscriptPdfExporter() throws Exception {
        // Dùng font Unicode tiếng Việt trong resources/fonts
        BaseFont regular = BaseFont.createFont(
                "fonts/NotoSans-Regular.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );
        BaseFont bold = BaseFont.createFont(
                "fonts/NotoSans-Bold.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );

        this.titleFont = new Font(bold, 16);
        this.headerFont = new Font(bold, 10);
        this.normalFont = new Font(regular, 10);
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text, font));
        c.setPadding(4f);
        return c;
    }

    private String score(BigDecimal d) {
        return d == null ? "" : d.stripTrailingZeros().toPlainString();
    }

    public void export(String courseId,
                       String courseName,
                       String semesterId,
                       String lecturerLabel,
                       List<ClassTranscriptItemDTO> rows,
                       OutputStream out) throws DocumentException {

        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        PdfWriter.getInstance(doc, out);
        doc.open();

        // --- Header ---
        Paragraph title = new Paragraph("BẢNG ĐIỂM LỚP HỌC", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        doc.add(new Paragraph("Môn học: " + courseId + " - " + (courseName == null ? "" : courseName), normalFont));
        doc.add(new Paragraph("Kỳ học: " + semesterId, normalFont));
        if (lecturerLabel != null && !lecturerLabel.isBlank()) {
            doc.add(new Paragraph("Giảng viên: " + lecturerLabel, normalFont));
        }
        doc.add(new Paragraph(
                "Ngày in: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                normalFont
        ));
        doc.add(Chunk.NEWLINE);

        // --- Bảng dữ liệu ---
        PdfPTable tbl = new PdfPTable(new float[]{1.2f, 3f, 5f, 2f, 2f, 2f, 2f});
        tbl.setWidthPercentage(100);

        tbl.addCell(cell("STT", headerFont));
        tbl.addCell(cell("Mã SV", headerFont));
        tbl.addCell(cell("Họ tên", headerFont));
        tbl.addCell(cell("QT", headerFont));
        tbl.addCell(cell("GK", headerFont));
        tbl.addCell(cell("CK", headerFont));
        tbl.addCell(cell("TK", headerFont));

        int i = 1;
        for (ClassTranscriptItemDTO row : rows) {
            tbl.addCell(cell(String.valueOf(i++), normalFont));
            tbl.addCell(cell(row.studentId(), normalFont));
            tbl.addCell(cell(row.studentName(), normalFont));
            tbl.addCell(cell(score(row.processScore()), normalFont));
            tbl.addCell(cell(score(row.midtermScore()), normalFont));
            tbl.addCell(cell(score(row.finalExamScore()), normalFont));
            tbl.addCell(cell(score(row.finalScore()), normalFont));
        }

        doc.add(tbl);
        doc.close();
    }
}
