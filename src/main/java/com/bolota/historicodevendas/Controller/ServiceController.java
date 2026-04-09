package com.bolota.historicodevendas.Controller;

import com.bolota.historicodevendas.Entities.DTO.ServiceEntityDTO;
import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.ServiceEntity;
import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.FixedSuppliesResource;
import com.bolota.historicodevendas.Resource.ServiceResource;
import com.bolota.historicodevendas.Resource.UserResource;
import com.bolota.historicodevendas.Resource.VariableSuppliesResource;
import com.bolota.historicodevendas.Service.ServicesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ServiceController {
    ServiceResource serviceResource;
    UserResource userResource;
    ServicesService servicesService;
    public ServiceController(ServiceResource serviceResource, UserResource userResource, ServicesService servicesService){
        this.servicesService = servicesService;
        this.userResource = userResource;
        this.serviceResource = serviceResource;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerService(@AuthenticationPrincipal Jwt jwt, @RequestBody ServiceEntityDTO peDTO){

        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (peDTO == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        if (peDTO.checkIfNull()) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        double totalExpenses = servicesService.getTotalExpenses(ue,peDTO);
        switch ((int) totalExpenses){
            case -1: return new ResponseEntity<>(HttpStatusCode.valueOf(400));
            case -2: return new ResponseEntity<>(HttpStatusCode.valueOf(406));
        }
        String UUIDgenerated = genUUID();
        ServiceEntity pe = new ServiceEntity(peDTO,totalExpenses,ue.getProfitMargin(), ue.getDesiredMonthlyIncome(), ue.getDaysWorkingWeekly(), ue.getHoursWorkingDaily());
        ServiceEntityPersistent pep = new ServiceEntityPersistent(pe, UUIDgenerated);
        ue.getServicesUUIDList().add(UUIDgenerated);
        userResource.save(ue);
        serviceResource.save(pep);
        return new ResponseEntity<>(UUIDgenerated,HttpStatusCode.valueOf(200));
    }
    @Transactional
    @DeleteMapping("/remove")
    public ResponseEntity<Void> deleteService(@AuthenticationPrincipal Jwt jwt, @RequestBody String productUUID){
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (productUUID == null) return ResponseEntity.status(400).build();
        ServiceEntityPersistent se = serviceResource.getByUUID(productUUID);
        if (se != null){
            servicesService.removeSupplies(se,ue,productUUID);
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(HttpStatusCode.valueOf(404));
    }
    public String genUUID(){
        char[] UUID_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder s;
        do{
            s = new StringBuilder();
            for (int i = 0; i < 20;i++){
                s.append(UUID_CHARS[(int)((Math.random() * 100) % UUID_CHARS.length)]);
            }
        }while (serviceResource.existsByUUID(s.toString()));
        return s.toString();
    }
}
