package com.bolota.historicodevendas.Controller;
import com.bolota.historicodevendas.Entities.DTO.MonthlyReportDTO;
import com.bolota.historicodevendas.Entities.DTO.ServiceReportDTO;
import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.FixedSuppliesResource;
import com.bolota.historicodevendas.Resource.ServiceResource;
import com.bolota.historicodevendas.Resource.UserResource;
import com.bolota.historicodevendas.Resource.VariableSuppliesResource;
import com.bolota.historicodevendas.Service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.security.Provider;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import static com.bolota.historicodevendas.Service.ProductService.toPage;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    UserResource userResource;

    @Autowired
    ServiceResource serviceResource;

    @Autowired
    VariableSuppliesResource variableSuppliesResource;


    @Autowired
    FixedSuppliesResource fixedSuppliesResource;

    @Autowired
    PdfGeneratorService pdfGeneratorService;

    @GetMapping("/services")
    public ResponseEntity<Page<ServiceEntityPersistent>> sendServices(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 10) Pageable pageable){
        if(jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        ArrayList<ServiceEntityPersistent> servicesInPossesion = new ArrayList<>();
        for (String UUIDs : ue.getServicesUUIDList()) {
            servicesInPossesion.add(serviceResource.getByUUID(UUIDs));
        }
        return ResponseEntity.ok().body(toPage(servicesInPossesion, pageable));
    }
    @GetMapping("/supplies")
    public ResponseEntity<Page<SuppliesEntityPersistent>> sendSupplies(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 10) Pageable pageable){
        if(jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (!userResource.existsByLogin(jwt.getSubject())) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ArrayList<SuppliesEntityPersistent> suppliesInPossesion = new ArrayList<>();
        for (String UUIDs : ue.getVariableSuppliesUsedUUID()) {
            suppliesInPossesion.add(variableSuppliesResource.getByUUID(UUIDs));
        }
        return ResponseEntity.ok().body(toPage(suppliesInPossesion, pageable));
    }
    @GetMapping("/supplies_fixed")
    public ResponseEntity<Page<FixedSuppliesEntityPersistent>> sendSuppliesFixed(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 10) Pageable pageable){
        if(jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (!userResource.existsByLogin(jwt.getSubject())) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ArrayList<FixedSuppliesEntityPersistent> suppliesInPossesion = new ArrayList<>();
        for (String UUIDs : ue.getFixedSuppliesUsedUUID()) {
            suppliesInPossesion.add(fixedSuppliesResource.getByUUID(UUIDs));
        }
        return ResponseEntity.ok().body(toPage(suppliesInPossesion, pageable));
    }
    @GetMapping(value = "/download_pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> sendSuppliesFixed(@AuthenticationPrincipal Jwt jwt,@RequestParam int month, @RequestParam int year){
        if (month == 0 || year == 0) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        LocalDate date = LocalDate.of(year,month,1);
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (!userResource.existsByLogin(jwt.getSubject())) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        LocalDate ldInit = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate ldEnd  = LocalDate.of(date.getYear(), date.getMonth(), date.lengthOfMonth());
        ArrayList<ServiceEntityPersistent> servicesInMonth = serviceResource.findByUUIDInAndServiceDateBetween(ue.getServicesUUIDList(),ldInit,ldEnd);
        if (servicesInMonth == null) return ResponseEntity.status(404).build();
        if (servicesInMonth.isEmpty()) return ResponseEntity.status(404).build();
        ArrayList<ServiceReportDTO> srDTOList = new ArrayList<>();
        int nmr = 0;
        for(ServiceEntityPersistent i : servicesInMonth) {
            srDTOList.add(new ServiceReportDTO(i));
        }
        double grossTotal = 0.0, liquidTotal= 0.0 ;
        for(ServiceReportDTO i : srDTOList) {
            grossTotal += i.getSalePrice();
            liquidTotal += i.getFinalProfit();
        }
        Locale locale = new Locale("pt", "BR");
        MonthlyReportDTO mrDTO = new MonthlyReportDTO(date.getMonth().getDisplayName(TextStyle.FULL, locale),date.getYear(),"Relatório consolidado dos serviços realizados no mês.",
                LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString(),ldInit.toString() + " até " + ldEnd.toString(),
                "Valores sujeitos a revisão. Em caso de dúvidas, entre em contato",
                grossTotal,liquidTotal,liquidTotal/srDTOList.size(),srDTOList.size(),srDTOList);
        byte[] pdfFile = pdfGeneratorService.generatePdf(mrDTO);
        String filename = "extrato-mensal-" + year + "-" + String.format("%02d", month) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfFile.length)
                .body(pdfFile);
    }
}
