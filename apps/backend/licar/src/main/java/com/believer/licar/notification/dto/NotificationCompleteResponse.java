package com.believer.licar.notification.dto;

import com.believer.licar.transport.entity.Transport;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationCompleteResponse {

    private final int transportId;
    private final LocalDateTime successTime;
    private final int nurseId;
    private final int pharmacistId;

    public static NotificationCompleteResponse from(Transport transport) {
        return NotificationCompleteResponse.builder()
                .transportId(transport.getTransportId())
                .successTime(transport.getArrivedTime())
                .nurseId(transport.getNurse().getNurseId())
                .pharmacistId(transport.getPharmacist().getPharmacistId())
                .build();
    }
}
