package com.believer.licar.prescription.dto;

import com.believer.licar.prescription.entity.Drug;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DrugCreateRequest {

    private String name;
    private String form;
    private Integer dosage;
    private String unit;

    public Drug toEntity() {
        return new Drug(name, form, dosage, unit);
    }
}
