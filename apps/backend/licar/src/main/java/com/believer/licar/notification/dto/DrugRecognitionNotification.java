package com.believer.licar.notification.dto;

import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.transport.entity.Transport;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrugRecognitionNotification {

    private final int transportId;
    private final int drugId;
    private final String drugName;
    private final boolean isRecognized;

    public static DrugRecognitionNotification from(Transport transport, Drug drug, boolean isRecognized) {
        return DrugRecognitionNotification.builder()
                .transportId(transport.getTransportId())
                .drugId(drug.getDrugId())
                .drugName(drug.getName())
                .isRecognized(isRecognized)
                .build();
    }
}
