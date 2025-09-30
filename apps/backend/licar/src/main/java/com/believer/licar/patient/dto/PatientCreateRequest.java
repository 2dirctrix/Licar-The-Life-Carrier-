package com.believer.licar.patient.dto;

import com.believer.licar.patient.entity.Patient;
import com.believer.licar.user.entity.Nurse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PatientCreateRequest {

    private String name;
    private LocalDate birthday;
    private LocalDate admissionDate;
    private Integer roomNumber;
    private int nurseId;

    public Patient toEntity(Nurse nurse) {
        return new Patient(
                this.name,
                this.birthday,
                this.admissionDate,
                this.roomNumber,
                nurse
        );
    }
}
