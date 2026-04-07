package com.bolota.historicodevendas.Controller;

import com.bolota.historicodevendas.Entities.DTO.UserEntityDTO;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    // recebe Json para popular hashmap modelo (String, String)
    // login:                    String login
    // password:                 String senha
    // desiredMonthlyIncome:     double income
    // daysWorkingWeekly:        double diasTrabalhando
    //  hoursWorkingDaily:       double diastrabalhados
    @Autowired
    UserResource userResource;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtEncoder jwtEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserEntityDTO ueDTO){
        if (ueDTO == null) return ResponseEntity.status(400).build();
        if (ueDTO.getLogin().trim().isEmpty() || ueDTO.getPasswordHash().trim().isEmpty() || ueDTO.getHoursWorkingDaily()>20 || ueDTO.getDaysWorkingWeekly()>7) return ResponseEntity.status(400).build();
        if (ueDTO.getDaysWorkingWeekly() == 0 || ueDTO.getDesiredMonthlyIncome() == 0 || ueDTO.getHoursWorkingDaily() == 0) return ResponseEntity.status(406).build();
        if (userResource.existsByLogin(ueDTO.getLogin())) return ResponseEntity.status(409).build();
        UserEntity ue = new UserEntity(ueDTO, passwordEncoder.encode(ueDTO.getPasswordHash()));
        userResource.save(ue);
        return ResponseEntity.ok().body(issueLoginToken(ue.getLogin()));
    }

    // hashmap<String, String> loginInfo ->
    // key: user     -> login
    // key: password -> password

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody HashMap<String, String> loginInfo){
        if (loginInfo == null) return ResponseEntity.status(400).build();
        if (loginInfo.get("login") == null || loginInfo.get("password") == null) return ResponseEntity.status(400).build();
        if (loginInfo.get("login").trim().isEmpty() || loginInfo.get("password").trim().isEmpty()) return ResponseEntity.status(400).build();
        if (!userResource.existsByLogin(loginInfo.get("login"))) return ResponseEntity.status(404).build();
        if (!passwordEncoder.matches(loginInfo.get("password"), userResource.getByLogin(loginInfo.get("login")).getPasswordHash())) return ResponseEntity.status(401).build();
        return ResponseEntity.ok().body(issueLoginToken(loginInfo.get("login")));
    }
    @PatchMapping("/update")
    public ResponseEntity<Void> updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserEntityDTO ueDTO){
        if (ueDTO == null) return ResponseEntity.status(400).build();
        if (jwt == null) return ResponseEntity.status(401).build();
        if (ueDTO.getDaysWorkingWeekly() <= 0 || ueDTO.getDesiredMonthlyIncome() <= 0 || ueDTO.getHoursWorkingDaily() == 0) return ResponseEntity.status(406).build();
        UserEntity ue = userResource.getByLogin(jwt.getSubject());
        if (ueDTO.getHoursWorkingDaily()>20 || ueDTO.getDaysWorkingWeekly()>7) return ResponseEntity.status(400).build();
        if (ue == null) return ResponseEntity.status(401).build();
        ue.updateUserInfo(ueDTO);
        userResource.save(ue);
        return ResponseEntity.status(200).build();
    }

    public String issueLoginToken(String login) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("HistoricoVendas")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60L * 60L * 3L))
                .subject(login)
                .claim("roles", List.of("USER"))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();
    }

}