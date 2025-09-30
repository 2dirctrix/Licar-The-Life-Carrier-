package com.believer.licar.patient.controller;

import com.believer.licar.patient.dto.PatientCreateRequest;
import com.believer.licar.patient.dto.PatientDetailResponse;
import com.believer.licar.patient.dto.PatientSimpleResponse;
import com.believer.licar.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    // POST /patients
    @PostMapping
    public ResponseEntity<PatientSimpleResponse> createPatient(@RequestBody PatientCreateRequest request) {
        PatientSimpleResponse createdPatient = patientService.createPatient(request);

        log.info("[ Patient Add ] {}", createdPatient.getPatientId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    // GET /patients
    @GetMapping
    public ResponseEntity<List<PatientSimpleResponse>> getAllPatients() {
        List<PatientSimpleResponse> patients = patientService.findAllPatients();

        log.info("[ Patient List ] {}", patients.size());
        return ResponseEntity.ok(patients);
    }

    // GET /patients?name={name}
    @GetMapping(params = "name")
    public ResponseEntity<List<PatientSimpleResponse>> searchPatientsByName(@RequestParam String name) {
        List<PatientSimpleResponse> patients = patientService.searchPatientsByName(name);

        log.info("[ Patient Search List ] {} - {} ", name, patients.size());
        return ResponseEntity.ok(patients);
    }

    // GET /patients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PatientDetailResponse> getPatientDetail(@PathVariable int id ) {
        PatientDetailResponse patient = patientService.findPatientById(id);

        log.info("[ Patient Detail ] {}", patient.getPatientId());
        return ResponseEntity.ok(patient);
    }
}
