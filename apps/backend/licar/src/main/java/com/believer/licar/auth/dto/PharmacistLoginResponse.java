package com.believer.licar.auth.dto;

import com.believer.licar.user.entity.Pharmacist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PharmacistLoginResponse {

    private final int pharmacistId;
    private final String name;

    public static PharmacistLoginResponse from(Pharmacist pharmacist) {
        return PharmacistLoginResponse.builder()
                .pharmacistId(pharmacist.getPharmacistId())
                .name(pharmacist.getName())
                .build();
    }
}
