package com.believer.licar.prescription.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class PrescriptionDrugId implements Serializable {

    private int prescription;
    private int drug;
}
