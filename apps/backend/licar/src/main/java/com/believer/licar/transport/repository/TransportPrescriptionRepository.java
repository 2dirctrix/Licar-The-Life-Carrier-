package com.believer.licar.transport.repository;

import com.believer.licar.transport.entity.TransportPrescription;
import com.believer.licar.transport.entity.TransportPrescriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportPrescriptionRepository extends JpaRepository<TransportPrescription, TransportPrescriptionId> {
}
