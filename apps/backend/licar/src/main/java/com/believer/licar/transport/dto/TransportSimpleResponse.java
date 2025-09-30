package com.believer.licar.transport.dto;

import com.believer.licar.transport.entity.Transport;
import com.believer.licar.transport.enums.TransportStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportSimpleResponse {

    private final int transportId;
    private final TransportStatus status;
    private final Integer robotId;
    private final String nurseName;
    private final Integer patientId;

    public static TransportSimpleResponse from(Transport transport) {
        return TransportSimpleResponse.builder()
                .transportId(transport.getTransportId())
                .status(transport.getStatus())
                .robotId(transport.getRobotId())
                .nurseName(transport.getNurse().getName())
                .patientId(transport.getPatient().getPatientId())
                .build();
    }
}
