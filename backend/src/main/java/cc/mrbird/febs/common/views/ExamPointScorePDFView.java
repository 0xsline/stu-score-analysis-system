package cc.mrbird.febs.common.views;

import cc.mrbird.febs.common.utils.SchoolConstants;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.Map;


public class ExamPointScorePDFView extends CustomAbstractPdfView {

    protected void buildPdfDocument(Map<String, Object> model, Document document,
                                    PdfWriter pdfWriter, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese);

        //logo
        Paragraph logoText = new Paragraph();
        logoText.setFont(fontChinese);
        File file = ResourceUtils.getFile("classpath:logo.png");
        Chunk logo = new Chunk(Image.getInstance(file.getAbsolutePath()), 0, -15);
        logoText.add(logo);
        logoText.add(SchoolConstants.BRAND_NAME);
        document.add(logoText);

        String title = (String) model.get("title");
        //标题
        Paragraph p = new Paragraph(title, fontChinese);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        //表头
        String[][] result = (String[][]) model.get("result");
        PdfPTable table = new PdfPTable(result[0].length);
        String[] tableTitle = result[0];
        for (int i = 0; i < tableTitle.length; i++) {
            PdfPCell cell = new PdfPCell(new Paragraph(tableTitle[i], fontChinese));
            cell.setBackgroundColor(BaseColor.CYAN);
            cell.setFixedHeight(25f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }


        //表体
        for (int i = 1; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                PdfPCell cell;
                if (j == 0 || j == 1) {
                    cell = new PdfPCell(new Paragraph(result[i][j], fontChinese));
                } else {
                    cell = new PdfPCell(new Paragraph(result[i][j]));
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setFixedHeight(25f);

                table.addCell(cell);
            }
        }

        float[] widths = new float[tableTitle.length];
        Arrays.fill(widths, 1.6f);
        widths[0] = 1.6f;
        table.setWidths(widths);

        document.add(table);
    }
}