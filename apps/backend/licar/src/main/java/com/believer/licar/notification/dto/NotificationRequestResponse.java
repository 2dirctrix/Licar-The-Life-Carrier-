package com.believer.licar.notification.dto;

import com.believer.licar.transport.entity.Transport;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationRequestResponse {

    private final int transportId;
    private final LocalDateTime requestedTime;
    private final int nurseId;
    private final int pharmacistId;

    public static NotificationRequestResponse from(Transport transport) {
        return NotificationRequestResponse.builder()
                .transportId(transport.getTransportId())
                .requestedTime(transport.getRequestedTime())
                .nurseId(transport.getNurse().getNurseId())
                .pharmacistId(transport.getPharmacist().getPharmacistId())
                .build();
    }
}
