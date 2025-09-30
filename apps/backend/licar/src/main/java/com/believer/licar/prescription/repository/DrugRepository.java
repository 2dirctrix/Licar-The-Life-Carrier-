package com.believer.licar.prescription.repository;

import com.believer.licar.prescription.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRepository extends JpaRepository<Drug, Integer> {
}
