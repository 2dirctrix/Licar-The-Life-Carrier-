package com.believer.licar.patient.entity;

import com.believer.licar.user.entity.Nurse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private int patientId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @Column(name = "room_number")
    private Integer roomNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nurse_id", nullable = false)
    private Nurse nurse;

    public Patient(String name, LocalDate birthday, LocalDate admissionDate, Integer roomNumber, Nurse nurse) {
        this.name = name;
        this.birthday = birthday;
        this.admissionDate = admissionDate;
        this.roomNumber = roomNumber;
        this.nurse = nurse;
    }
}
