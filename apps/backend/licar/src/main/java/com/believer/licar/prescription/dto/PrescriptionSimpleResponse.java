package com.believer.licar.prescription.dto;

import com.believer.licar.prescription.entity.Prescription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PrescriptionSimpleResponse {

    private final int prescriptionId;
    private final int patientId;
    private final String patientName;
    private final LocalDate prescriptionDate;

    public static PrescriptionSimpleResponse from(Prescription prescription) {
        return PrescriptionSimpleResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .patientId(prescription.getPatient().getPatientId())
                .patientName(prescription.getPatient().getName())
                .prescriptionDate(prescription.getPrescriptionDate())
                .build();
    }
}
