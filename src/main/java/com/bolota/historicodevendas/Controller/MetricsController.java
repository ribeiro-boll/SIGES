package com.bolota.historicodevendas.Controller;
import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.FixedSuppliesResource;
import com.bolota.historicodevendas.Resource.ServiceResource;
import com.bolota.historicodevendas.Resource.UserResource;
import com.bolota.historicodevendas.Resource.VariableSuppliesResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import java.security.Provider;
import java.util.ArrayList;

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

    @GetMapping("/services")
    public ResponseEntity<Page<ServiceEntityPersistent>> sendServices(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 10) Pageable pageable){
        if(jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(404).build();
        ArrayList<ServiceEntityPersistent> servicesInPossesion = new ArrayList<>();
        for (String UUIDs : ue.getServicesUUIDList()) {
            servicesInPossesion.add(serviceResource.getByUUID(UUIDs));
        }
        return ResponseEntity.ok().body(toPage(servicesInPossesion, pageable));
    }
    @GetMapping("/supplies")
    public ResponseEntity<Page<SuppliesEntityPersistent>> sendSupplies(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 10) Pageable pageable){
        if(jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(404).build();
        if (!userResource.existsByLogin(jwt.getSubject())) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ArrayList<SuppliesEntityPersistent> suppliesInPossesion = new ArrayList<>();
        for (String UUIDs : ue.getVariableSuppliesUsedUUID()) {
            suppliesInPossesion.add(variableSuppliesResource.getByUUID(UUIDs));
        }
        return ResponseEntity.ok().body(toPage(suppliesInPossesion, pageable));
    }
    @GetMapping("/supplies_fixed")
    public ResponseEntity<Page<FixedSuppliesEntityPersistent>> sendSuppliesFixed(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 10) Pageable pageable){
        if(jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(404).build();
        if (!userResource.existsByLogin(jwt.getSubject())) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ArrayList<FixedSuppliesEntityPersistent> suppliesInPossesion = new ArrayList<>();
        for (String UUIDs : ue.getFixedSuppliesUsedUUID()) {
            suppliesInPossesion.add(fixedSuppliesResource.getByUUID(UUIDs));
        }
        return ResponseEntity.ok().body(toPage(suppliesInPossesion, pageable));
    }
}
