package com.believer.licar.prescription.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class PrescriptionCreateRequest {

    private int patientId;
    private LocalDate prescriptionDate;
    private Integer period;
    private String counsellingNote;
    private List<Integer> drugIds;
}
