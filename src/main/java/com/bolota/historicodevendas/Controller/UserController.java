package com.bolota.historicodevendas.Controller;

import com.bolota.historicodevendas.Entities.DTO.UserEntityDTO;
import com.bolota.historicodevendas.Entities.UserEntity;
import com.bolota.historicodevendas.Resource.UserResource;
import com.bolota.historicodevendas.Service.JwtService;
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
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    UserResource userResource;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    public UserController(JwtService jwtService, PasswordEncoder passwordEncoder, UserResource userResource){
        this.userResource = userResource;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserEntityDTO ueDTO){
        if (ueDTO == null) return ResponseEntity.status(400).build();
        if (ueDTO.getLogin().trim().isEmpty() || ueDTO.getPasswordHash().trim().isEmpty() || ueDTO.getHoursWorkingDaily()>20 || ueDTO.getDaysWorkingWeekly()>7) return ResponseEntity.status(400).build();
        if (ueDTO.getDaysWorkingWeekly() == 0 || ueDTO.getDesiredMonthlyIncome() == 0 || ueDTO.getHoursWorkingDaily() == 0) return ResponseEntity.status(406).build();
        if (userResource.existsByLogin(ueDTO.getLogin())) return ResponseEntity.status(409).build();
        UserEntity ue = new UserEntity(ueDTO, passwordEncoder.encode(ueDTO.getPasswordHash()));
        userResource.save(ue);
        return ResponseEntity.ok().body(jwtService.issueLoginToken(ue.getLogin()));
    }

    // hashmap<String, String> loginInfo ->
    // key: user     -> login
    // key: password -> password

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginInfo){
        if (loginInfo == null) return ResponseEntity.status(400).build();
        if (loginInfo.get("login") == null || loginInfo.get("password") == null) return ResponseEntity.status(400).build();
        if (loginInfo.get("login").trim().isEmpty() || loginInfo.get("password").trim().isEmpty()) return ResponseEntity.status(400).build();
        if (!userResource.existsByLogin(loginInfo.get("login"))) return ResponseEntity.status(404).build();
        if (!passwordEncoder.matches(loginInfo.get("password"), userResource.getByLogin(loginInfo.get("login")).getPasswordHash())) return ResponseEntity.status(401).build();
        return ResponseEntity.ok().body(jwtService.issueLoginToken(loginInfo.get("login")));
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
}