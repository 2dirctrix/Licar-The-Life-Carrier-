package com.believer.licar.prescription.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescription_drugs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PrescriptionDrugId.class)
public class PrescriptionDrug {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drug_id")
    private Drug drug;

    public PrescriptionDrug(Prescription prescription, Drug drug) {
        this.prescription = prescription;
        this.drug = drug;
    }
}
