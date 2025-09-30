package com.believer.licar.transport.dto;

import com.believer.licar.prescription.entity.Prescription;
import com.believer.licar.transport.entity.Transport;
import com.believer.licar.transport.enums.TransportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TransportDetailResponse {

    private final int transportId;
    private final LocalDateTime requestedTime;
    private final LocalDateTime sentTime;
    private final LocalDateTime arrivedTime;
    private final TransportStatus status;
    private final Integer robotId;
    private final String nurseName;
    private final String pharmacistName;
    private final Integer patientId;
    private final List<PrescriptionInfo> prescriptions;

    @Getter
    @Builder
    public static class PrescriptionInfo {
        private final int prescriptionId;
        private final String patientName;
        private final LocalDate prescriptionDate;
        private final String counsellingNote;

        static PrescriptionInfo from(Prescription prescription) {
            return PrescriptionInfo.builder()
                    .prescriptionId(prescription.getPrescriptionId())
                    .patientName(prescription.getPatient().getName())
                    .prescriptionDate(prescription.getPrescriptionDate())
                    .counsellingNote(prescription.getCounsellingNote())
                    .build();
        }
    }

    public static TransportDetailResponse from(Transport transport) {
        List<PrescriptionInfo> prescriptionInfos = transport.getTransportPrescriptions().stream()
                .map(tp -> PrescriptionInfo.from(tp.getPrescription()))
                .collect(Collectors.toList());

        return TransportDetailResponse.builder()
                .transportId(transport.getTransportId())
                .requestedTime(transport.getRequestedTime())
                .sentTime(transport.getSentTime())
                .arrivedTime(transport.getArrivedTime())
                .status(transport.getStatus())
                .robotId(transport.getRobotId())
                .nurseName(transport.getNurse().getName())
                .pharmacistName(transport.getPharmacist().getName())
                .patientId(transport.getPatient().getPatientId())
                .prescriptions(prescriptionInfos)
                .build();
    }
}
