package com.believer.licar.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PharmacistLoginRequest {

    private int pharmacistId;
    private String name;
}
