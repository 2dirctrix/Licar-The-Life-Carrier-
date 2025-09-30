package com.believer.licar.prescription.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drugs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_id")
    private int drugId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "form", length = 20)
    private String form;

    @Column(name = "dosage")
    private Integer dosage;

    @Column(name = "unit", length = 20)
    private String unit;

    public Drug(String name, String form, Integer dosage, String unit) {
        this.name = name;
        this.form = form;
        this.dosage = dosage;
        this.unit = unit;
    }
}
