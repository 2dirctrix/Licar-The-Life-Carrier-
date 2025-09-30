package com.believer.licar.transport.entity;

import com.believer.licar.transport.enums.TransportStatus;
import com.believer.licar.patient.entity.Patient;
import com.believer.licar.user.entity.Nurse;
import com.believer.licar.user.entity.Pharmacist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transport_id")
    private int transportId;

    @Column(name = "requested_time")
    private LocalDateTime requestedTime;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Column(name = "arrived_time")
    private LocalDateTime arrivedTime;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "transport_status")
    private TransportStatus status;

    @Column(name = "robot_id", nullable = false)
    private Integer robotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", nullable = false)
    private Nurse nurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacist_id", nullable = false)
    private Pharmacist pharmacist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportPrescription> transportPrescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportDrugRecognition> transportDrugRecognitions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.requestedTime = LocalDateTime.now();
        this.status = TransportStatus.requested;
    }

    public Transport(Integer robotId, Nurse nurse, Pharmacist pharmacist, Patient patient) {
        this.robotId = robotId;
        this.nurse = nurse;
        this.pharmacist = pharmacist;
        this.patient = patient;
    }

    public void startTransit() {
        this.status = TransportStatus.in_transit;
        this.sentTime = LocalDateTime.now();
    }

    public void completeTransport() {
        this.status = TransportStatus.completed;
        this.arrivedTime = LocalDateTime.now();
    }
}
