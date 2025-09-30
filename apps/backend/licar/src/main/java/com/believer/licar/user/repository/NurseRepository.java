package com.believer.licar.user.repository;

import com.believer.licar.user.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NurseRepository extends JpaRepository<Nurse, Integer> {

    Optional<Nurse> findByNurseIdAndName(int nurseId, String name);
}
