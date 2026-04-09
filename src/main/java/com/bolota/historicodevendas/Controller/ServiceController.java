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
    @Autowired
    ServiceResource serviceResource;
    @Autowired
    VariableSuppliesResource variableSuppliesResource;
    @Autowired
    FixedSuppliesResource fixedSuppliesResource;
    @Autowired
    UserController userController;
    @Autowired
    private UserResource userResource;

    @PostMapping("/register")
    public ResponseEntity<String> registerService(@AuthenticationPrincipal Jwt jwt, @RequestBody ServiceEntityDTO peDTO){
        int condInvalid = 0;
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (peDTO == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        if (peDTO.checkIfNull()) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        if (!peDTO.getVariableSuppliesUsedUUID().isEmpty()){
            SuppliesEntityPersistent tempSupply;
            for(int i =0; i< peDTO.getVariableSuppliesUsedUUID().size(); i++){
                if (!variableSuppliesResource.existsByUUID(peDTO.getVariableSuppliesUsedUUID().get(i))){
                    return new ResponseEntity<>(HttpStatusCode.valueOf(400));
                }
                if (peDTO.getVariableSuppliesQuantityUsed().get(peDTO.getVariableSuppliesUsedUUID().get(i))== 0){
                    return new ResponseEntity<>(HttpStatusCode.valueOf(406));
                }
                tempSupply = variableSuppliesResource.getByUUID(peDTO.getVariableSuppliesUsedUUID().get(i));
                int quantity = tempSupply.getCounterInUseByServices();
                tempSupply.setCounterInUseByServices(quantity+ 1);
                variableSuppliesResource.save(tempSupply);
                tempSupply = null;
            }
        }
        else{
            condInvalid++;
        }
        if (!peDTO.getFixedSuppliesUsedUUID().isEmpty()){
            FixedSuppliesEntityPersistent tempSupplyFixed;
            for(int i =0; i< peDTO.getFixedSuppliesUsedUUID().size(); i++){
                if (!fixedSuppliesResource.existsByUUID(peDTO.getFixedSuppliesUsedUUID().get(i))){
                    return new ResponseEntity<>(HttpStatusCode.valueOf(400));
                }
                tempSupplyFixed = fixedSuppliesResource.getByUUID(peDTO.getFixedSuppliesUsedUUID().get(i));
                int quantity = tempSupplyFixed.getCounterInUseByServices();
                tempSupplyFixed.setCounterInUseByServices(quantity+ 1);
                fixedSuppliesResource.save(tempSupplyFixed);
                tempSupplyFixed = null;
            }
        }
        else {
            condInvalid++;
        }
        if (condInvalid == 2) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        double totalExpenses = 0.0;
        for(int i = 0; i< peDTO.getVariableSuppliesUsedUUID().size(); i++){
            totalExpenses += variableSuppliesResource.getByUUID(peDTO.getVariableSuppliesUsedUUID().get(i)).getCostPerMeasure() * peDTO.getVariableSuppliesQuantityUsed().get(peDTO.getVariableSuppliesUsedUUID().get(i));
        }
        for(int i = 0; i< peDTO.getFixedSuppliesUsedUUID().size(); i++){
            totalExpenses +=fixedSuppliesResource.getByUUID(peDTO.getFixedSuppliesUsedUUID().get(i)).getCostPerMinute() * peDTO.getAverageServiceDurationMinutes();
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
            SuppliesEntityPersistent tempSupply;
            if (se.getVariableSuppliesUsedUUID() != null){
                if (!se.getVariableSuppliesUsedUUID().isEmpty()){
                    for(int j = 0; j < se.getVariableSuppliesUsedUUID().size() ;j++) {
                        tempSupply = variableSuppliesResource.getByUUID(se.getVariableSuppliesUsedUUID().get(j));
                        int tempCounter = tempSupply.getCounterInUseByServices();
                        tempSupply.setCounterInUseByServices(tempCounter - 1);
                        variableSuppliesResource.save(tempSupply);
                        tempSupply = null;
                    }
                }
            }
            FixedSuppliesEntityPersistent fixedTempSupply;
            if (se.getFixedSuppliesUsedUUID() != null) {
                if (!se.getFixedSuppliesUsedUUID().isEmpty()) {
                    for (int j = 0; j < se.getFixedSuppliesUsedUUID().size(); j++) {
                        fixedTempSupply = fixedSuppliesResource.getByUUID(se.getFixedSuppliesUsedUUID().get(j));
                        int tempCounter = fixedTempSupply.getCounterInUseByServices();
                        fixedTempSupply.setCounterInUseByServices(tempCounter - 1);
                        fixedSuppliesResource.save(fixedTempSupply);
                        fixedTempSupply = null;
                    }
                }
            }
            ue.getServicesUUIDList().remove(productUUID);
            serviceResource.deleteByUUID(productUUID);
            userResource.save(ue);
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
