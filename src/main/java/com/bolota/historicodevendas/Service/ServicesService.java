package com.bolota.historicodevendas.Service;

import com.bolota.historicodevendas.Entities.DTO.ServiceEntityDTO;
import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.ServiceEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.FixedSuppliesResource;
import com.bolota.historicodevendas.Resource.ServiceResource;
import com.bolota.historicodevendas.Resource.UserResource;
import com.bolota.historicodevendas.Resource.VariableSuppliesResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServicesService {
    VariableSuppliesResource variableSuppliesResource;

    FixedSuppliesResource fixedSuppliesResource;

    UserResource userResource;

    ServiceResource serviceResource;

    public ServicesService(VariableSuppliesResource variableSuppliesResource, FixedSuppliesResource fixedSuppliesResource, UserResource userResource, ServiceResource serviceResource) {
        this.variableSuppliesResource = variableSuppliesResource;
        this.fixedSuppliesResource = fixedSuppliesResource;
        this.userResource = userResource;
        this.serviceResource = serviceResource;
    }


    public double getTotalExpenses(UserEntity ue, ServiceEntityDTO peDTO){
        int condInvalid = 0;
        if (!peDTO.getVariableSuppliesUsedUUID().isEmpty()){
            SuppliesEntityPersistent tempSupply;
            for(int i =0; i< peDTO.getVariableSuppliesUsedUUID().size(); i++){
                if (!variableSuppliesResource.existsByUUID(peDTO.getVariableSuppliesUsedUUID().get(i))){
                    return -1;
                }
                if (peDTO.getVariableSuppliesQuantityUsed().get(peDTO.getVariableSuppliesUsedUUID().get(i))== 0){
                    return -2;
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
                    return -1;
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
        if (condInvalid == 2) return -1;
        double totalExpenses = 0.0;
        for(int i = 0; i< peDTO.getVariableSuppliesUsedUUID().size(); i++){
            totalExpenses += variableSuppliesResource.getByUUID(peDTO.getVariableSuppliesUsedUUID().get(i)).getCostPerMeasure() * peDTO.getVariableSuppliesQuantityUsed().get(peDTO.getVariableSuppliesUsedUUID().get(i));
        }
        for(int i = 0; i< peDTO.getFixedSuppliesUsedUUID().size(); i++){
            totalExpenses +=fixedSuppliesResource.getByUUID(peDTO.getFixedSuppliesUsedUUID().get(i)).getCostPerMinute() * peDTO.getAverageServiceDurationMinutes();
        }
        return totalExpenses;
    }
    public void removeSupplies(ServiceEntityPersistent se, UserEntity ue, String productUUID){
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
    }
}
