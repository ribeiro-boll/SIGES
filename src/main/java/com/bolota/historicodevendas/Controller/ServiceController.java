package com.bolota.historicodevendas.Controller;

import com.bolota.historicodevendas.Entities.DTO.ProductEntityDTO;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.ServiceEntity;
import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import com.bolota.historicodevendas.Entities.SuppliesEntity;
import com.bolota.historicodevendas.Entities.UserEntity;
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

@RestController
@RequestMapping("/product")
public class ServiceController {
    @Autowired
    ServiceResource serviceResource;
    @Autowired
    VariableSuppliesResource variableSuppliesResource;
    @Autowired
    UserController userController;
    @Autowired
    private UserResource userResource;

    @PostMapping("/register")
    public ResponseEntity<Void> registerProduct(@AuthenticationPrincipal Jwt jwt, @RequestBody ProductEntityDTO peDTO){
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
        double totalExpenses = 0.0;
        for(int i = 0; i< peDTO.getVariableSuppliesUsedUUID().size(); i++){
            totalExpenses += variableSuppliesResource.getByUUID(peDTO.getVariableSuppliesUsedUUID().get(i)).getCostPerMeasure() * peDTO.getVariableSuppliesQuantityUsed().get(peDTO.getVariableSuppliesUsedUUID().get(i));
        }
        String UUIDgenerated = genUUID();
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        //public ServiceEntity(ProductEntityDTO peDTO, double serviceExpensesprivate, double profitMargin,double desiredAmountPerMonth,int daysWorking, double hoursWorking){
        ServiceEntity pe = new ServiceEntity(peDTO,totalExpenses,ue.getProfitMargin(), ue.getDesiredMonthlyIncome(), ue.getDaysWorkingWeekly(), ue.getHoursWorkingDaily());
        ServiceEntityPersistent pep = new ServiceEntityPersistent(pe, UUIDgenerated);
        ue.getServicesUUIDList().add(UUIDgenerated);
        userResource.save(ue);
        serviceResource.save(pep);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
    @Transactional
    @DeleteMapping("/remove")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal Jwt jwt, @RequestBody String productUUID){
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        if (!userResource.existsByLogin(jwt.getSubject())) return  new ResponseEntity<>(HttpStatusCode.valueOf(404));
        UserEntity temporaryUser = userResource.getByLogin(jwt.getSubject());
        for (int i = 0; i < temporaryUser.getServicesUUIDList().size(); i++) {
            if (!productUUID.equals(temporaryUser.getServicesUUIDList().get(i))) {
                continue;
            } else {
                SuppliesEntityPersistent tempSupply;
                ServiceEntityPersistent tempEntity = serviceResource.getByUUID(productUUID);
                for(int j = 0; j < tempEntity.getSuppliesUsed().size() ;j++) {
                    tempSupply = variableSuppliesResource.getByUUID(tempEntity.getSuppliesUsed().get(j));
                    int tempCounter = tempSupply.getCounterInUseByServices();
                    tempSupply.setCounterInUseByServices(tempCounter - 1);
                    variableSuppliesResource.save(tempSupply);
                    tempSupply = null;
                }
                temporaryUser.getServicesUUIDList().remove(i);
                serviceResource.deleteByUUID(productUUID);
                userResource.save(temporaryUser);
                return new ResponseEntity<>(HttpStatusCode.valueOf(200));
            }
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
