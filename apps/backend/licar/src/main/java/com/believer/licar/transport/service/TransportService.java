package com.believer.licar.transport.service;

import com.believer.licar.notification.dto.DrugRecognitionNotification;
import com.believer.licar.prescription.entity.Drug;
import com.believer.licar.prescription.entity.Prescription;
import com.believer.licar.prescription.entity.PrescriptionDrug;
import com.believer.licar.prescription.repository.DrugRepository;
import com.believer.licar.prescription.repository.PrescriptionRepository;
import com.believer.licar.transport.dto.*;
import com.believer.licar.transport.entity.Transport;
import com.believer.licar.transport.entity.TransportDrugRecognition;
import com.believer.licar.transport.entity.TransportDrugRecognitionId;
import com.believer.licar.transport.entity.TransportPrescription;
import com.believer.licar.transport.enums.TransportStatus;
import com.believer.licar.transport.repository.TransportDrugRecognitionRepository;
import com.believer.licar.transport.repository.TransportRepository;
import com.believer.licar.global.exception.CustomException;
import com.believer.licar.global.exception.ErrorCode;
import com.believer.licar.notification.dto.NotificationCompleteResponse;
import com.believer.licar.notification.dto.NotificationRequestResponse;
import com.believer.licar.notification.service.NotificationService;
import com.believer.licar.patient.entity.Patient;
import com.believer.licar.patient.repository.PatientRepository;
import com.believer.licar.prescription.dto.DrugResponse;
import com.believer.licar.prescription.service.PrescriptionService;
import com.believer.licar.user.entity.Nurse;
import com.believer.licar.user.entity.Pharmacist;
import com.believer.licar.user.repository.NurseRepository;
import com.believer.licar.user.repository.PharmacistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;
    private final NurseRepository nurseRepository;
    private final PharmacistRepository pharmacistRepository;
    private final PrescriptionService prescriptionService;
    private final PrescriptionRepository prescriptionRepository;
    private final TransportDrugRecognitionRepository transportDrugRecognitionRepository;
    private final DrugRepository drugRepository;

    @Transactional
    public TransportDetailResponse requestTransport(TransportCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new CustomException(ErrorCode.PATIENT_NOT_FOUND));
        Nurse nurse = nurseRepository.findById(request.getNurseId())
                .orElseThrow(() -> new CustomException(ErrorCode.NURSE_NOT_FOUND));
        Pharmacist pharmacist = pharmacistRepository.findById(request.getPharmacistId())
                .orElseThrow(() -> new CustomException(ErrorCode.PHARMACIST_NOT_FOUND));

        Transport transport = new Transport(request.getRobotId(), nurse, pharmacist, patient);

        List<Prescription> validPrescriptions = prescriptionRepository.findValidPrescriptionsForPatient(patient.getPatientId(), LocalDate.now());

        validPrescriptions.forEach(prescription -> {
            TransportPrescription tp = new TransportPrescription(transport, prescription);
            transport.getTransportPrescriptions().add(tp);

            prescription.getPrescriptionDrugs().forEach(pd -> {
                transport.getTransportDrugRecognitions().add(new TransportDrugRecognition(transport, pd.getDrug()));
            });
        });

        Transport savedTransport = transportRepository.save(transport);

        // SSE
        NotificationRequestResponse notification = NotificationRequestResponse.from(savedTransport);
        notificationService.sendToPharmacist(savedTransport.getPharmacist().getPharmacistId(), notification);

        return TransportDetailResponse.from(savedTransport);
    }

    @Transactional
    public TransportSimpleResponse startTransport(int transportId) {
        Transport transport = findTransportEntityById(transportId);

        if (transport.getStatus() != TransportStatus.requested) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        transport.startTransit();

        return TransportSimpleResponse.from(transport);
    }

    @Transactional
    public TransportSimpleResponse completeTransport(int transportId) {
        Transport transport = findTransportEntityById(transportId);

        if (transport.getStatus() != TransportStatus.in_transit) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        transport.completeTransport();

        // SSE
        NotificationCompleteResponse notification = NotificationCompleteResponse.from(transport);
        notificationService.sendToNurse(transport.getNurse().getNurseId(), notification);

        return TransportSimpleResponse.from(transport);
    }

    @Transactional(readOnly = true)
    public TransportDetailResponse findTransportById(int transportId) {

        return TransportDetailResponse.from(findTransportEntityById(transportId));
    }

    @Transactional(readOnly = true)
    public List<TransportSimpleResponse> findAllTransports() {
        return transportRepository.findAll().stream()
                .map(TransportSimpleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DrugResponse> findValidDrugsForTransport(int transportId) {
        Transport transport = findTransportEntityById(transportId);
        int patientId = transport.getPatient().getPatientId();
        LocalDate requestDate = transport.getRequestedTime().toLocalDate();

        return prescriptionService.findValidDrugsForPatient(patientId, requestDate);
    }

    private Transport findTransportEntityById(int transportId) {
        return transportRepository.findById(transportId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSPORT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<DrugResponse> findRequestedDrugsForLatestTransport() {
        Transport latestTransport = transportRepository.findFirstByOrderByTransportIdDesc()
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSPORT_NOT_FOUND));

        if (latestTransport.getStatus() != TransportStatus.requested) {
            throw new CustomException(ErrorCode.TRANSPORT_NOT_FOUND);
        }

        int patientId = latestTransport.getPatient().getPatientId();

//        return prescriptionService.findValidDrugsForPatient(patientId, LocalDate.now());
        return latestTransport.getTransportPrescriptions().stream()
                .map(TransportPrescription::getPrescription)
                .flatMap(prescription -> prescription.getPrescriptionDrugs().stream())
                .map(PrescriptionDrug::getDrug)
                .distinct()
                .map(DrugResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RobotTransportInfoResponse getLatestTransportDetailsForRobot(int robotId) {
        Transport latestTransport = transportRepository.findFirstByRobotIdOrderByTransportIdDesc(robotId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSPORT_NOT_FOUND));

        return RobotTransportInfoResponse.from(latestTransport);
    }

    @Transactional
    public void updateDrugRecognitionStatus(int transportId, int drugId) {
        Transport transport = findTransportEntityById(transportId);
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new CustomException(ErrorCode.DRUG_NOT_FOUND));

        TransportDrugRecognitionId id = new TransportDrugRecognitionId(transportId, drugId);
        TransportDrugRecognition recognition = transportDrugRecognitionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        recognition.recognize();

        DrugRecognitionNotification notification = DrugRecognitionNotification.from(transport, drug, true);

        // notificationService.sendDrugRecognitionToNurse(transport.getNurse().getNurseId(), notification);

        notificationService.sendDrugRecognitionToPharmacist(transport.getPharmacist().getPharmacistId(), notification);
    }

    @Transactional(readOnly = true)
    public TransportPrescriptionDetailResponse findPrescriptionDetailsForTransport(int transportId, int prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESCRIPTION_NOT_FOUND));

        Transport transport = findTransportEntityById(transportId);

        boolean isPrescriptionInTransport = transport.getTransportPrescriptions().stream()
                .anyMatch(tp -> tp.getPrescription().getPrescriptionId() == prescriptionId);

        if (!isPrescriptionInTransport) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<TransportDrugRecognition> recognitions = transport.getTransportDrugRecognitions();

        return TransportPrescriptionDetailResponse.from(prescription, recognitions);
    }
}
