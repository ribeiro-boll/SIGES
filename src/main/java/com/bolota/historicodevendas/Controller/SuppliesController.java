package com.bolota.historicodevendas.Controller;

import com.bolota.historicodevendas.Entities.DTO.ProductEntityDTO;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.SuppliesEntity;
import com.bolota.historicodevendas.Entities.UserEntity;
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
@RequestMapping("/supplies")
public class SuppliesController {
    @Autowired
    VariableSuppliesResource variableSuppliesResource;

    @Autowired
    UserResource userResource;

    //TODO: implementar autenticação usando token JWT
    @PostMapping("/register")
    public ResponseEntity<Void> registerSupply(@AuthenticationPrincipal Jwt jwt, @RequestBody SuppliesEntity se){
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        if (!userResource.existsByLogin(jwt.getSubject()) ) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        if (se.getProductValue() == 0 || se.getName().trim().isBlank() || se.getMeasure() == 0){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
        String UUIDgen = genUUID();
        SuppliesEntityPersistent sep = new SuppliesEntityPersistent(se, UUIDgen);
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        ue.getVariableSuppliesUsedUUID().add(UUIDgen);
        userResource.save(ue);
        variableSuppliesResource.save(sep);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @Transactional
    @DeleteMapping("/remove")
    public ResponseEntity<Void> deleteSupply(@AuthenticationPrincipal Jwt jwt, @RequestBody String productUUID) {
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        if (!userResource.existsByLogin(jwt.getSubject())) return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        if (variableSuppliesResource.getByUUID(productUUID) == null){
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        }
        if (variableSuppliesResource.getByUUID(productUUID).getCounterInUseByServices() !=0){
            return new ResponseEntity<>(HttpStatusCode.valueOf(406));
        }
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if(!ue.getVariableSuppliesUsedUUID().contains((String)productUUID)) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ue.getVariableSuppliesUsedUUID().remove((String)productUUID);
        variableSuppliesResource.removeByUUID(productUUID);
        userResource.save(ue);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
    public String genUUID(){
        char[] UUID_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder s;
        do{
            s = new StringBuilder();
            for (int i = 0; i < 20;i++){
                s.append(UUID_CHARS[(int)((Math.random() * 100) % UUID_CHARS.length)]);
            }
        }while (variableSuppliesResource.existsByUUID(s.toString()));
        return s.toString();
    }
}
