package com.bolota.historicodevendas.Controller;

import com.bolota.historicodevendas.Entities.FixedSuppliesEntity;
import com.bolota.historicodevendas.Entities.PersistentEntities.FixedSuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.PersistentEntities.SuppliesEntityPersistent;
import com.bolota.historicodevendas.Entities.SuppliesEntity;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.FixedSuppliesResource;
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
    FixedSuppliesResource fixedSuppliesResource;

    @Autowired
    UserResource userResource;

    @PostMapping("/register")
    public ResponseEntity<String> registerSupply(@AuthenticationPrincipal Jwt jwt, @RequestBody SuppliesEntity se){
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null || se == null) return ResponseEntity.status(401).build();
        if (se.getProductValue() <= 0 || se.getName().trim().isBlank() || se.getMeasure() <= 0){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
        String UUIDgen = genUUID();
        SuppliesEntityPersistent sep = new SuppliesEntityPersistent(se, UUIDgen);
        ue.getVariableSuppliesUsedUUID().add(UUIDgen);
        userResource.save(ue);
        variableSuppliesResource.save(sep);
        return new ResponseEntity<>(UUIDgen,HttpStatusCode.valueOf(200));
    }
    @PostMapping("/register_fixed")
    public ResponseEntity<String> registerFixedSupply(@AuthenticationPrincipal Jwt jwt, @RequestBody FixedSuppliesEntity fe){
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null || fe == null) return ResponseEntity.status(401).build();
        if (fe.getSuppliesValue() <= 0 || fe.getName().trim().isBlank()){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
        String UUIDgen = genUUID();
        FixedSuppliesEntityPersistent sep = new FixedSuppliesEntityPersistent(fe, UUIDgen);
        ue.getFixedSuppliesUsedUUID().add(UUIDgen);
        userResource.save(ue);
        fixedSuppliesResource.save(sep);
        return new ResponseEntity<>(UUIDgen,HttpStatusCode.valueOf(200));
    }

    @Transactional
    @DeleteMapping("/remove_fixed")
    public ResponseEntity<Void> deleteFixedSupply(@AuthenticationPrincipal Jwt jwt, @RequestBody String productUUID) {
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        FixedSuppliesEntityPersistent fse= fixedSuppliesResource.getByUUID(productUUID);
        if (fse == null) return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        if (fse.getCounterInUseByServices() !=0) return new ResponseEntity<>(HttpStatusCode.valueOf(406));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if(!ue.getFixedSuppliesUsedUUID().contains((String)productUUID)) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ue.getFixedSuppliesUsedUUID().remove((String)productUUID);
        fixedSuppliesResource.removeByUUID(productUUID);
        userResource.save(ue);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
    @Transactional
    @DeleteMapping("/remove")
    public ResponseEntity<Void> deleteSupply(@AuthenticationPrincipal Jwt jwt, @RequestBody String productUUID) {
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        SuppliesEntityPersistent vse = variableSuppliesResource.getByUUID(productUUID);
        if (vse == null) return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        if (vse.getCounterInUseByServices() != 0) return new ResponseEntity<>(HttpStatusCode.valueOf(406));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if (!ue.getVariableSuppliesUsedUUID().contains((String) productUUID)) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        ue.getVariableSuppliesUsedUUID().remove((String) productUUID);
        variableSuppliesResource.removeByUUID(productUUID);
        userResource.save(ue);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @PatchMapping("/edit_fixedSupply")
    public ResponseEntity<Void> editFixedSuppply(@AuthenticationPrincipal Jwt jwt, @RequestBody FixedSuppliesEntityPersistent fsep) {
        if (jwt == null) return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        if (fsep == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        if (fsep.getUUID() == null) return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        FixedSuppliesEntityPersistent fsepLocalEntity = fixedSuppliesResource.getByUUID(fsep.getUUID());
        if (fsepLocalEntity == null) return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ue == null) return ResponseEntity.status(401).build();
        if(!ue.getFixedSuppliesUsedUUID().contains((String)fsep.getUUID())) return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        fsepLocalEntity.setCounterInUseByServices(fsep.getCounterInUseByServices());
        fsepLocalEntity.setDescription(fsep.getDescription());
        fsepLocalEntity.setFixedSupplyDate(fsep.getFixedSupplyDate());
        fsepLocalEntity.setName(fsep.getName());
        fsepLocalEntity.setSupplyTotalCost(fsep.getSupplyTotalCost());
        fsepLocalEntity.setCondUpdatePopup(fsep.isCondUpdatePopup());
        fsepLocalEntity.generateCostPerMinute();
        fixedSuppliesResource.save(fsepLocalEntity);
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
