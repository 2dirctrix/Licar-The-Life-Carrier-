package com.believer.licar.transport.dto;

import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.prescription.entity.Prescription;
import com.believer.licar.transport.entity.TransportDrugRecognition;
import com.believer.licar.transport.entity.TransportPrescription;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
public class TransportPrescriptionDetailResponse {

    private final int prescriptionId;
    private final int period;
    private final String patientName;
    private final String counsellingNote;
    private final List<DrugWithRecognitionStatus> drugs;

    @Getter
    @Builder
    private static class DrugWithRecognitionStatus {

        private final int drugId;
        private final String name;
        private final Integer dosage;
        private final String unit;
        private final boolean isRecognized;

        static DrugWithRecognitionStatus from(Drug drug, boolean isRecognized) {
            return DrugWithRecognitionStatus.builder()
                    .drugId(drug.getDrugId())
                    .name(drug.getName())
                    .dosage(drug.getDosage())
                    .unit(drug.getUnit())
                    .isRecognized(isRecognized)
                    .build();
        }
    }

    public static TransportPrescriptionDetailResponse from(Prescription prescription, List<TransportDrugRecognition> recognitions) {

        Map<Integer, Boolean> recognitionMap = recognitions.stream()
                .collect(Collectors.toMap(
                        rec -> rec.getDrug().getDrugId(),
                        TransportDrugRecognition::isRecognized
                ));

        List<DrugWithRecognitionStatus> drugDetails = prescription.getPrescriptionDrugs().stream()
                .map(pd -> {
                    Drug drug = pd.getDrug();

                    boolean recognized = recognitionMap.getOrDefault(drug.getDrugId(), false);

                    return DrugWithRecognitionStatus.from(drug, recognized);
                })
                .collect(Collectors.toList());

        return TransportPrescriptionDetailResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .period(prescription.getPeriod())
                .patientName(prescription.getPatient().getName())
                .counsellingNote(prescription.getCounsellingNote())
                .drugs(drugDetails)
                .build();
    }
}
