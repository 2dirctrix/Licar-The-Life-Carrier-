package com.believer.licar.patient.dto;

import com.believer.licar.patient.entity.Patient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientSimpleResponse {

    private final int patientId;
    private final String patientName;
    private final Integer roomNumber;

    public static PatientSimpleResponse from(Patient patient) {
        return PatientSimpleResponse.builder()
                .patientId(patient.getPatientId())
                .patientName(patient.getName())
                .roomNumber(patient.getRoomNumber())
                .build();
    }
}
