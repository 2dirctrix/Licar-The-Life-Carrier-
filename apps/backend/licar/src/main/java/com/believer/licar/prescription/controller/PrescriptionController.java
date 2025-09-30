package com.believer.licar.prescription.controller;

import com.believer.licar.prescription.dto.*;
import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.prescription.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
@Slf4j
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // POST /prescriptions/drugs
    @PostMapping("/drugs")
    public ResponseEntity<Drug> createDrug(@RequestBody DrugCreateRequest request) {
        Drug createdDrug = prescriptionService.createDrug(request);

        log.info("[ Drug Add ] {}", createdDrug.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDrug);
    }

    // POST /prescriptions
    @PostMapping
    public ResponseEntity<PrescriptionDetailResponse> createPrescription(@RequestBody PrescriptionCreateRequest request) {
        PrescriptionDetailResponse prescription = prescriptionService.createPrescription(request);

        log.info("[ Prescription Detail ] {}", prescription.getPrescriptionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
    }

    // GET /prescriptions
    @GetMapping
    public ResponseEntity<List<PrescriptionSimpleResponse>> getPrescription(@RequestParam(required = false) Integer patient_id) {
        if (patient_id != null) {
            List<PrescriptionSimpleResponse> prescriptions = prescriptionService.findPrescriptionsByPatientId(patient_id);

            log.info("[ Prescription by Patient ] {} - {}", patient_id, prescriptions.size());
            return ResponseEntity.ok(prescriptions);
        }

        List<PrescriptionSimpleResponse> prescriptions = prescriptionService.findAllPrescriptions();

        log.info("[ Prescription List ] {}", prescriptions.size());
        return ResponseEntity.ok(prescriptions);
    }

    // GET /prescriptions/{prescription_id}
    @GetMapping("/{prescription_id}")
    public ResponseEntity<PrescriptionDetailResponse> getPrescriptionDetail(@PathVariable("prescription_id") int prescriptionId) {
        PrescriptionDetailResponse prescription = prescriptionService.findPrescriptionById(prescriptionId);

        log.info("[ Prescription Detail ] {}", prescription.getPrescriptionId());
        return ResponseEntity.ok(prescription);
    }

    // GET /prescriptions/valid?patient_id={patient_id}&date={yyyy-MM-dd}
    @GetMapping("/valid")
    public ResponseEntity<List<PrescriptionSimpleResponse>> getValidPrescriptions(
            @RequestParam("patient_id") int patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<PrescriptionSimpleResponse> prescriptions = prescriptionService.findValidPrescriptionsForPatient(patientId, date);

        log.info("[ Prescription by patient and date ] {}", prescriptions.size());
        return ResponseEntity.ok(prescriptions);
    }

    // GET /prescriptions/valid-drugs?patient_id={id}&date={yyyy-MM-dd}
    @GetMapping("/valid-drugs")
    public ResponseEntity<List<DrugResponse>> getValidDrugs(
            @RequestParam("patient_id") int patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<DrugResponse> drugs = prescriptionService.findValidDrugsForPatient(patientId, date);

        log.info("[ Drug by patient and date ] {}", drugs.size());
        return ResponseEntity.ok(drugs);
    }
}
