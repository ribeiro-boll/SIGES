package com.bolota.historicodevendas.Service;

import com.bolota.historicodevendas.Entities.DTO.MonthlyReportDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public PdfGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderHtml(MonthlyReportDTO report) {
        Context context = new Context();
        context.setVariable("report", report);
        return templateEngine.process("monthly_report_template_compact", context);
    }

    public byte[] generatePdf(MonthlyReportDTO report) {
        String html = renderHtml(report);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório mensal", e);
        }
    }
}