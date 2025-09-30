package com.believer.licar.transport.entity;

import com.believer.licar.prescription.entity.Prescription;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transport_prescriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TransportPrescriptionId.class)
public class TransportPrescription {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id")
    private Transport transport;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    public TransportPrescription(Transport transport, Prescription prescription) {
        this.transport = transport;
        this.prescription = prescription;
    }
}
