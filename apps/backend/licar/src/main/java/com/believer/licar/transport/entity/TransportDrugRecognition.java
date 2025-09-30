package com.believer.licar.transport.entity;

import com.believer.licar.prescription.entity.Drug;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transport_drug_recognitions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TransportDrugRecognitionId.class)
public class TransportDrugRecognition {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id")
    private Transport transport;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @Column(name = "is_recognized")
    private boolean isRecognized = false;

    public TransportDrugRecognition(Transport transport, Drug drug) {
        this.transport = transport;
        this.drug = drug;
    }

    public void recognize() {
        this.isRecognized = true;
    }
}
