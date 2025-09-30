package com.believer.licar.prescription.repository;

import com.believer.licar.prescription.entity.PrescriptionDrug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionDrugRepository extends JpaRepository<PrescriptionDrug, Integer> {
}
