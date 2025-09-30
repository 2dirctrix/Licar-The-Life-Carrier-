package com.believer.licar.transport.dto;

import com.believer.licar.prescription.dto.DrugResponse;
import com.believer.licar.prescription.entity.PrescriptionDrug;
import com.believer.licar.transport.entity.Transport;
import com.believer.licar.transport.entity.TransportPrescription;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class RobotTransportInfoResponse {

    private final int transportId;
    private final int roomNumber;
    private final List<DrugResponse> drugs;

    public static RobotTransportInfoResponse from(Transport transport) {
        List<DrugResponse> drugList = transport.getTransportPrescriptions().stream()
                .map(TransportPrescription::getPrescription)
                .flatMap(prescription -> prescription.getPrescriptionDrugs().stream())
                .map(PrescriptionDrug::getDrug)
                .distinct()
                .map(DrugResponse::from)
                .collect(Collectors.toList());

        return RobotTransportInfoResponse.builder()
                .transportId(transport.getTransportId())
                .roomNumber(transport.getPatient().getRoomNumber())
                .drugs(drugList)
                .build();
    }
}
