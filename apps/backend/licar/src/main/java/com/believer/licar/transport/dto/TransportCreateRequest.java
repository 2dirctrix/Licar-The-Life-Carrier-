package com.believer.licar.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransportCreateRequest {

    private Integer robotId;
    private int nurseId;
    private int pharmacistId;
    private int patientId;
}
