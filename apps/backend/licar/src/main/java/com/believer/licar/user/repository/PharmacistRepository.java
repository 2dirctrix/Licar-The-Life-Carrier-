package com.believer.licar.user.repository;

import com.believer.licar.user.entity.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacistRepository extends JpaRepository<Pharmacist, Integer> {

    Optional<Pharmacist> findByPharmacistIdAndName(int pharmacistId, String name);
}