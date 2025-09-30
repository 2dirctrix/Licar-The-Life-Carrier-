package com.believer.licar.prescription.dto;

import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.prescription.repository.DrugRepository;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrugResponse {
    private final int drugId;
    private final String name;
    private final String form;
    private final Integer dosage;
    private final String unit;

    public static DrugResponse from(Drug drug) {
        return DrugResponse.builder()
                .drugId(drug.getDrugId())
                .name(drug.getName())
                .form(drug.getForm())
                .dosage(drug.getDosage())
                .unit(drug.getUnit())
                .build();
    }
}
