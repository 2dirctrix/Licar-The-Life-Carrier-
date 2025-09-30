package com.believer.licar.prescription.entity;

import com.believer.licar.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private int prescriptionId;

    @Column(name = "prescription_date")
    private LocalDate prescriptionDate;

    @Column(name = "period")
    private Integer period;

    @Column(name = "counselling_note")
    private String counsellingNote;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<PrescriptionDrug> prescriptionDrugs = new ArrayList<>();

    public Prescription(Patient patient, LocalDate date, Integer period, String counsellingNote) {
        this.patient = patient;
        this.prescriptionDate = date;
        this.period = period;
        this.counsellingNote = counsellingNote;
    }
}
