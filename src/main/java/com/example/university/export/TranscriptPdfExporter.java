package com.example.university.export;

import com.example.university.dto.GpaDTO;
import com.example.university.dto.TranscriptItemDTO;
import com.example.university.entity.Student;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfGState;
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

    private static final Font TITLE_FONT;
    private static final Font STRONG_FONT;
    private static final Font NORMAL_FONT;

    static {
        // Ưu tiên font Unicode tiếng Việt, fallback sang Helvetica nếu lỗi
        Font title;
        Font strong;
        Font normal;
        try {
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

            title = new Font(bold, 16);
            strong = new Font(bold, 11);
            normal = new Font(regular, 11);
        } catch (Exception e) {
            title = new Font(Font.HELVETICA, 16, Font.BOLD);
            strong = new Font(Font.HELVETICA, 11, Font.BOLD);
            normal = new Font(Font.HELVETICA, 11, Font.NORMAL);
        }

        TITLE_FONT = title;
        STRONG_FONT = strong;
        NORMAL_FONT = normal;
    }

    private static String fmt(BigDecimal x) {
        return x == null ? "–" : x.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static byte[] build(Student sv, List<TranscriptItemDTO> rows, GpaDTO gpa, String semester) throws Exception {
        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        doc.open();

        addWatermark(writer, doc);

        // Tiêu đề
        Paragraph title = new Paragraph("BẢNG ĐIỂM SINH VIÊN", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        // Thông tin SV
        Paragraph p1 = new Paragraph();
        p1.setFont(NORMAL_FONT);
        p1.add("Sinh viên: "); p1.add(new Chunk(sv.getFullName() + "    ", STRONG_FONT));
        p1.add("Mã sinh viên: "); p1.add(new Chunk(sv.getId() + "\n", STRONG_FONT));
        if (sv.getDateOfBirth() != null) {
            p1.add("Ngày sinh: "); p1.add(new Chunk(sv.getDateOfBirth().toString() + "    ", STRONG_FONT));
        }
        if (sv.getDepartmentID() != null) {
            p1.add("Khoa: "); p1.add(new Chunk(sv.getDepartmentID() + "\n", STRONG_FONT));
        }
        String ky = (semester == null || semester.isBlank())
                ? "Tích lũy toàn khoá"
                : semester;
        p1.add("Kỳ: "); p1.add(new Chunk(ky, STRONG_FONT));
        doc.add(p1);
        doc.add(Chunk.NEWLINE);

        // GPA
        if (gpa != null) {
            Paragraph p2 = new Paragraph(
                    "GPA hệ 10: " + (gpa.gpa10() == null ? "0.00" : String.format("%.2f", gpa.gpa10()))
                            + "    GPA hệ 4: " + (gpa.gpa4() == null ? "0.00" : String.format("%.2f", gpa.gpa4())),
                    NORMAL_FONT
            );
            doc.add(p2);
            doc.add(Chunk.NEWLINE);
        }

        // Bảng điểm
        PdfPTable table = new PdfPTable(new float[]{2.2f, 5.2f, 1.0f, 1.2f, 1.2f, 1.2f, 1.5f, 1.8f});
        table.setWidthPercentage(100);

        addHeader(table, "Mã môn", STRONG_FONT);
        addHeader(table, "Tên môn", STRONG_FONT);
        addHeader(table, "Số tín chỉ", STRONG_FONT);
        addHeader(table, "Điểm quá trình", STRONG_FONT);
        addHeader(table, "Điểm giữa kỳ", STRONG_FONT);
        addHeader(table, "Điểm cuối kỳ", STRONG_FONT);
        addHeader(table, "Tổng kết", STRONG_FONT);
        addHeader(table, "Kỳ học", STRONG_FONT);

        for (TranscriptItemDTO r : rows) {
            addCell(table, r.courseId(), NORMAL_FONT, Element.ALIGN_LEFT);
            addCell(table, r.courseName(), NORMAL_FONT, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(r.credits()), NORMAL_FONT, Element.ALIGN_CENTER);
            addCell(table, fmt(r.processScore()), NORMAL_FONT, Element.ALIGN_RIGHT);
            addCell(table, fmt(r.midtermScore()), NORMAL_FONT, Element.ALIGN_RIGHT);
            addCell(table, fmt(r.finalExamScore()), NORMAL_FONT, Element.ALIGN_RIGHT);
            addCell(table, fmt(r.finalScore()), NORMAL_FONT, Element.ALIGN_RIGHT);
            addCell(table, r.semesterId(), NORMAL_FONT, Element.ALIGN_CENTER);
        }

        doc.add(table);
        doc.add(Chunk.NEWLINE);

        Paragraph footer = new Paragraph("Ngày xuất: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), NORMAL_FONT);
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

    private static void addWatermark(PdfWriter writer, Document doc) {
        try {
            var logoUrl = TranscriptPdfExporter.class
                    .getClassLoader()
                    .getResource("static/assets/logo-ptit.png");
            if (logoUrl == null) return;

            Image logo = Image.getInstance(logoUrl);
            logo.scaleToFit(260, 260);

            float x = (doc.getPageSize().getWidth() - logo.getScaledWidth()) / 2;
            float y = (doc.getPageSize().getHeight() - logo.getScaledHeight()) / 2;
            logo.setAbsolutePosition(x, y);

            PdfContentByte under = writer.getDirectContentUnder();
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.15f);

            under.saveState();
            under.setGState(gs);
            under.addImage(logo);
            under.restoreState();
        } catch (Exception ignored) {
            // Nếu không tải được logo, bỏ qua watermark để tránh chặn việc xuất PDF
        }
    }
}
