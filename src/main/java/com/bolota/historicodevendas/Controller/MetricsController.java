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
import java.util.List;
import java.util.Locale;

import static com.bolota.historicodevendas.Service.SuppliesService.toPage;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    UserResource userResource;
    ServiceResource serviceResource;
    VariableSuppliesResource variableSuppliesResource;
    FixedSuppliesResource fixedSuppliesResource;
    PdfGeneratorService pdfGeneratorService;
    public MetricsController(UserResource userResource, ServiceResource serviceResource, VariableSuppliesResource variableSuppliesResource, FixedSuppliesResource fixedSuppliesResource, PdfGeneratorService pdfGeneratorService){
        this.userResource = userResource;
        this.serviceResource = serviceResource;
        this.variableSuppliesResource  =variableSuppliesResource;
        this.fixedSuppliesResource = fixedSuppliesResource;
        this.pdfGeneratorService = pdfGeneratorService;
    }
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
        MonthlyReportDTO mrDTO = pdfGeneratorService.generateMonthlyReportDTO(ue,date);
        if (mrDTO == null){
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
        String filename = "extrato-mensal-" + year + "-" + String.format("%02d", month) + ".pdf";
        byte[] pdfFile = pdfGeneratorService.generatePdf(mrDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfFile.length)
                .body(pdfFile);
    }
}
