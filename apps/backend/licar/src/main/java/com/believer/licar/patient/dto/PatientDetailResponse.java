package com.believer.licar.patient.dto;

import com.believer.licar.patient.entity.Patient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PatientDetailResponse {

    private final int patientId;
    private final String patientName;
    private final LocalDate birthday;
    private final LocalDate admissionDate;
    private final LocalDate dischargeDate;
    private final Integer roomNumber;
    private final String nurseName;

    public static PatientDetailResponse from(Patient patient) {
        return PatientDetailResponse.builder()
                .patientId(patient.getPatientId())
                .patientName(patient.getName())
                .birthday(patient.getBirthday())
                .admissionDate(patient.getAdmissionDate())
                .dischargeDate(patient.getDischargeDate())
                .roomNumber(patient.getRoomNumber())
                .nurseName(patient.getNurse().getName())
                .build();
    }
}
