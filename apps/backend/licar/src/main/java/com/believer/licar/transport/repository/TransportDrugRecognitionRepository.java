package com.believer.licar.transport.repository;

import com.believer.licar.transport.entity.Transport;
import com.believer.licar.transport.entity.TransportDrugRecognition;
import com.believer.licar.transport.entity.TransportDrugRecognitionId;
import com.believer.licar.transport.entity.TransportPrescriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportDrugRecognitionRepository extends JpaRepository<TransportDrugRecognition, TransportDrugRecognitionId> {
}
