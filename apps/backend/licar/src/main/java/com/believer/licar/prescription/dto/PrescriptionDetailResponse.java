package com.believer.licar.prescription.dto;

import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.prescription.entity.Prescription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PrescriptionDetailResponse {

    private final int prescriptionId;
    private final String patientName;
    private final LocalDate prescriptionDate;
    private final Integer period;
    private final String counsellingNote;
    private final List<DrugInfo> drugs;

    @Getter
    @Builder
    private static class DrugInfo {
        private final String name;
        private final String form;
        private final Integer dosage;
        private final String unit;

        static DrugInfo from(Drug drug) {
            return DrugInfo.builder()
                    .name(drug.getName())
                    .form(drug.getForm())
                    .dosage(drug.getDosage())
                    .unit(drug.getUnit())
                    .build();
        }
    }

    public static PrescriptionDetailResponse from(Prescription prescription) {
        List<DrugInfo> drugInfos = prescription.getPrescriptionDrugs().stream()
                .map(prescriptionDrug -> DrugInfo.from(prescriptionDrug.getDrug()))
                .collect(Collectors.toList());

        return PrescriptionDetailResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .patientName(prescription.getPatient().getName())
                .prescriptionDate(prescription.getPrescriptionDate())
                .period(prescription.getPeriod())
                .counsellingNote(prescription.getCounsellingNote())
                .drugs(drugInfos)
                .build();
    }
}
