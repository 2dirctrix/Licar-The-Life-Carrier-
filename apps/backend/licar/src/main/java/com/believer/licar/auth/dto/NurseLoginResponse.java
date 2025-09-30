package com.believer.licar.auth.dto;

import com.believer.licar.user.entity.Nurse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NurseLoginResponse {

    private final int nurseId;
    private final String name;
    private final String department;

    public static NurseLoginResponse from(Nurse nurse) {
        return NurseLoginResponse.builder()
                .nurseId(nurse.getNurseId())
                .name(nurse.getName())
                .department(nurse.getDepartment())
                .build();
    }
}
