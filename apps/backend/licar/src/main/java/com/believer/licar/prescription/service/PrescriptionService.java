package com.believer.licar.prescription.service;

import com.believer.licar.global.exception.CustomException;
import com.believer.licar.global.exception.ErrorCode;
import com.believer.licar.patient.entity.Patient;
import com.believer.licar.patient.repository.PatientRepository;
import com.believer.licar.prescription.dto.*;
import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.prescription.entity.Prescription;
import com.believer.licar.prescription.entity.PrescriptionDrug;
import com.believer.licar.prescription.repository.DrugRepository;
import com.believer.licar.prescription.repository.PrescriptionDrugRepository;
import com.believer.licar.prescription.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DrugRepository drugRepository;
    private final PrescriptionDrugRepository prescriptionDrugRepository;

    @Transactional
    public Drug createDrug(DrugCreateRequest request) {
        Drug drug = request.toEntity();

        return drugRepository.save(drug);
    }

    @Transactional
    public PrescriptionDetailResponse createPrescription(PrescriptionCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));

        Prescription prescription = new Prescription(patient, request.getPrescriptionDate(), request.getPeriod(), request.getCounsellingNote());
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        for (Integer drugId : request.getDrugIds()) {
            Drug drug = drugRepository.findById(drugId)
                    .orElseThrow(() -> new CustomException(ErrorCode.DRUG_NOT_FOUND));
            PrescriptionDrug prescriptionDrug = new PrescriptionDrug(savedPrescription, drug);
            prescriptionDrugRepository.save(prescriptionDrug);
        }

        return findPrescriptionById(savedPrescription.getPrescriptionId());
    }

    @Transactional(readOnly = true)
    public List<PrescriptionSimpleResponse> findAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(PrescriptionSimpleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrescriptionSimpleResponse> findPrescriptionsByPatientId(int patientId) {
        return prescriptionRepository.findByPatientPatientId(patientId).stream()
                .map(PrescriptionSimpleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrescriptionDetailResponse findPrescriptionById(int prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESCRIPTION_NOT_FOUND));

        return PrescriptionDetailResponse.from(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionSimpleResponse> findValidPrescriptionsForPatient(int patientId, LocalDate targetDate) {
        List<Prescription> validPrescriptions = prescriptionRepository.findValidPrescriptionsForPatient(patientId, targetDate);

        return validPrescriptions.stream()
                .map(PrescriptionSimpleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrugResponse> findValidDrugsForPatient(int patientId, LocalDate targetDate) {
        List<Prescription> validPrescriptions = prescriptionRepository.findValidPrescriptionsForPatient(patientId, targetDate);

        return validPrescriptions.stream()
                .flatMap(prescription -> prescription.getPrescriptionDrugs().stream())
                .map(PrescriptionDrug::getDrug)
                .distinct()
                .map(DrugResponse::from)
                .collect(Collectors.toList());
    }
}
