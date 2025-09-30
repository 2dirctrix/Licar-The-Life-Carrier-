package com.believer.licar.transport.controller;

import com.believer.licar.transport.dto.TransportCreateRequest;
import com.believer.licar.transport.dto.TransportDetailResponse;
import com.believer.licar.transport.dto.TransportPrescriptionDetailResponse;
import com.believer.licar.transport.dto.TransportSimpleResponse;
import com.believer.licar.transport.service.TransportService;
import com.believer.licar.prescription.dto.DrugResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transports")
@RequiredArgsConstructor
@Slf4j
public class TransportController {

    private final TransportService transportService;

    // POST /transports
    @PostMapping
    public ResponseEntity<TransportDetailResponse> requestTransport(@RequestBody TransportCreateRequest request) {
        log.info("{} {} {} {}", request.getRobotId(), request.getNurseId(), request.getPharmacistId(), request.getPatientId());
        TransportDetailResponse transport = transportService.requestTransport(request);

        log.info("[ Transport Create ] {}", transport.getTransportId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transport);
    }

    // PATCH /transports/{transport_id}/start
    @PatchMapping("/{transport_id}/start")
    public ResponseEntity<TransportSimpleResponse> startTransport(@PathVariable("transport_id") int transportId) {
        TransportSimpleResponse transport = transportService.startTransport(transportId);

        log.info("[ Transport Start ] {}", transport.getTransportId());
        return ResponseEntity.ok(transport);
    }

    // PATCH /transports/{transport_id}/complete
    @PatchMapping("/{transport_id}/complete")
    public ResponseEntity<TransportSimpleResponse> completeTransport(@PathVariable("transport_id") int transportId) {
        TransportSimpleResponse transport = transportService.completeTransport(transportId);

        log.info("[ Transport Arrived ] {}", transport.getTransportId());
        return ResponseEntity.ok(transport);
    }

    // GET /transports/{transport_id}
    @GetMapping("/{transport_id}")
    public ResponseEntity<TransportDetailResponse> getTransport(@PathVariable("transport_id") int transportId) {
        TransportDetailResponse transport = transportService.findTransportById(transportId);

        log.info("[ Transport Detail ] {}", transport.getTransportId());
        return ResponseEntity.ok(transport);
    }

    // GET /transports
    @GetMapping
    public ResponseEntity<List<TransportSimpleResponse>> getAllTransports() {
        List<TransportSimpleResponse> transports = transportService.findAllTransports();

        log.info("[ Transport List ] {}", transports.size());
        return ResponseEntity.ok(transports);
    }

    // GET /transports/{transport_id}/valid-drugs
    @GetMapping("/{transport_id}/valid-drugs")
    public ResponseEntity<List<DrugResponse>> getValidDrugsForTransport(@PathVariable("transport_id") int transportId) {
        List<DrugResponse> drugs = transportService.findValidDrugsForTransport(transportId);

        log.info("[ Drug List by transport ] {} - {}", transportId, drugs.size());
        return ResponseEntity.ok(drugs);
    }

    // GET /transports/requested-drugs
    @GetMapping("/requested-drugs")
    public ResponseEntity<List<DrugResponse>> getRequestedDrugsForLatestTransport() {
        List<DrugResponse> response = transportService.findRequestedDrugsForLatestTransport();

        log.info("[ Drug List by latest transport ] {} ", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transport_id}/prescriptions/{prescription_id}")
    public ResponseEntity<TransportPrescriptionDetailResponse> getPrescriptionDetailsForTransport(
            @PathVariable("transport_id") int transportId, @PathVariable("prescription_id") int prescriptionId) {
        TransportPrescriptionDetailResponse response = transportService.findPrescriptionDetailsForTransport(transportId, prescriptionId);

        log.info("[ Prescription Detail by transport & prescription ] {}", response.getPrescriptionId());
        return ResponseEntity.ok(response);
    }
}
