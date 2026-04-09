package com.bolota.historicodevendas.Service;

import com.bolota.historicodevendas.Entities.DTO.MonthlyReportDTO;
import com.bolota.historicodevendas.Entities.DTO.ServiceReportDTO;
import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.ServiceResource;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    private TemplateEngine templateEngine;
    ServiceResource serviceResource;
    public PdfGeneratorService(ServiceResource serviceResource, TemplateEngine templateEngine){
        this.serviceResource = serviceResource;
        this.templateEngine = templateEngine;
    };


    public String renderHtml(MonthlyReportDTO report) {
        Context context = new Context();
        context.setVariable("report", report);
        return templateEngine.process("monthly_report_template_compact", context);
    }

    public MonthlyReportDTO generateMonthlyReportDTO(UserEntity ue,LocalDate date){
        LocalDate ldInit = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate ldEnd  = LocalDate.of(date.getYear(), date.getMonth(), date.lengthOfMonth());
        List<ServiceEntityPersistent> servicesInMonth = serviceResource.findByUUIDInAndServiceDateBetween(ue.getServicesUUIDList(),ldInit,ldEnd);
        ArrayList<ServiceReportDTO> srDTOList = new ArrayList<>();
        int nmr = 0;
        if (servicesInMonth == null) return null;
        if (servicesInMonth.isEmpty()) return null;
        for(ServiceEntityPersistent i : servicesInMonth) {
            srDTOList.add(new ServiceReportDTO(i));
        }

        double grossTotal = 0.0, liquidTotal= 0.0 ;
        for(ServiceReportDTO i : srDTOList) {
            grossTotal += i.getSalePrice();
            liquidTotal += i.getFinalProfit();
        }
        Locale locale = new Locale("pt", "BR");
        return new MonthlyReportDTO(date.getMonth().getDisplayName(TextStyle.FULL, locale),date.getYear(),"Relatório consolidado dos serviços realizados no mês.",
                LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString(),ldInit.toString() + " até " + ldEnd.toString(),
                "Valores sujeitos a revisão. Em caso de dúvidas, entre em contato",
                grossTotal,liquidTotal,liquidTotal/srDTOList.size(),srDTOList.size(),srDTOList);

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