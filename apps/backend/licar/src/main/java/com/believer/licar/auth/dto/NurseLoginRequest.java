package com.believer.licar.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NurseLoginRequest {

    private int nurseId;
    private String name;
}
