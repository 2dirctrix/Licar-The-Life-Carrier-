package com.believer.licar.prescription.repository;

import com.believer.licar.prescription.entity.Prescription;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    List<Prescription> findByPatientPatientId(int patientId);

    @Query(value = "SELECT * FROM prescriptions p " +
            "WHERE p.patient_id = :patientId AND :targetDate " +
            "BETWEEN p.prescription_date AND (p.prescription_date + p.period * INTERVAL '1 day')", nativeQuery = true)
    List<Prescription> findValidPrescriptionsForPatient(@Param("patientId") int patientId, @Param("targetDate")LocalDate targetDate);
}
