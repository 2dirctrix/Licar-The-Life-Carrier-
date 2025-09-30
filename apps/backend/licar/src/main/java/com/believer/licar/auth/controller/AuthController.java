package com.believer.licar.auth.controller;

import com.believer.licar.auth.dto.NurseLoginRequest;
import com.believer.licar.auth.dto.NurseLoginResponse;
import com.believer.licar.auth.dto.PharmacistLoginRequest;
import com.believer.licar.auth.dto.PharmacistLoginResponse;
import com.believer.licar.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/nurse/login")
    public ResponseEntity<NurseLoginResponse> loginNurse(@RequestBody NurseLoginRequest request) {
        log.info("{} {}", request.getNurseId(), request.getName());
        NurseLoginResponse response = authService.loginNurse(request);

        log.info("[ Nurse Login ] {}", response.getNurseId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pharmacist/login")
    public ResponseEntity<PharmacistLoginResponse> loginPharmacist(@RequestBody PharmacistLoginRequest request) {
        PharmacistLoginResponse response = authService.loginPharmacist(request);

        log.info("[ Pharmacist Login ] {}", response.getPharmacistId());
        return ResponseEntity.ok(response);
    }
}
