package com.believer.licar.notification.controller;

import com.believer.licar.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/pharmacist/{pharmacistId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribePharmacist(@PathVariable Integer pharmacistId) {
        log.info("[ Pharmacist Subscribe ] {}", pharmacistId);
        return notificationService.subscribePharmacist(pharmacistId);
    }

    @GetMapping(value = "/subscribe/nurse/{nurseId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeNurse(@PathVariable Integer nurseId) {
        log.info("[ Nurse Subscribe ] {}", nurseId);
        return notificationService.subscribeNurse(nurseId);
    }
}
