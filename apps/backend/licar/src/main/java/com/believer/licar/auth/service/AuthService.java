package com.believer.licar.auth.service;

import com.believer.licar.auth.dto.NurseLoginRequest;
import com.believer.licar.auth.dto.NurseLoginResponse;
import com.believer.licar.auth.dto.PharmacistLoginRequest;
import com.believer.licar.auth.dto.PharmacistLoginResponse;
import com.believer.licar.global.exception.CustomException;
import com.believer.licar.global.exception.ErrorCode;
import com.believer.licar.user.entity.Nurse;
import com.believer.licar.user.entity.Pharmacist;
import com.believer.licar.user.repository.NurseRepository;
import com.believer.licar.user.repository.PharmacistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final NurseRepository nurseRepository;
    private final PharmacistRepository pharmacistRepository;

    public NurseLoginResponse loginNurse(NurseLoginRequest request) {
        Nurse nurse = nurseRepository.findByNurseIdAndName(request.getNurseId(), request.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.NURSE_AUTHENTICATION_FAILED));

        return NurseLoginResponse.from(nurse);
    }

    public PharmacistLoginResponse loginPharmacist(PharmacistLoginRequest request) {
        Pharmacist pharmacist = pharmacistRepository.findByPharmacistIdAndName((request.getPharmacistId()), request.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.PHARMACIST_AUTHENTICATION_FAILED));

        return PharmacistLoginResponse.from(pharmacist);
    }
}
