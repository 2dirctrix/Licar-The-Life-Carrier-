package com.believer.licar.notification.service;

import com.believer.licar.global.config.MqttConfig;
import com.believer.licar.transport.dto.RobotTransportInfoResponse;
import com.believer.licar.transport.service.TransportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.ServiceActivators;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Getter
class DrugRecognitionPayload {
    private int transportId;
    private int drugId;
}

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttMessageService {

    private final TransportService transportService;
    private final MqttConfig.MqttGateway mqttGateway;
    private final ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<String> message) {
        Map<String, Object> headers = message.getHeaders();
        String payload = message.getPayload();

        String topic = (String) headers.get(MqttHeaders.RECEIVED_TOPIC);
        log.info("Received MQTT message on topic [{}]: {}", topic, payload);

        if (topic.startsWith("robots/status/")) {
            processRobotStatus(topic, payload);
        } else if ("transports/recognition/update".equals(topic)) {
            processDrugRecognition(payload);
        } else {
            log.warn("Received message on unhandled topic : {}", topic);
        }
    }

    private void processRobotStatus(String topic, String payload) {
        try {
            int robotId = Integer.parseInt(topic.substring(topic.lastIndexOf('/') + 1));
            RobotTransportInfoResponse responseDto = transportService.getLatestTransportDetailsForRobot(robotId);
            String jsonResponse = objectMapper.writeValueAsString(responseDto);
            String responseTopic = "robots/response/" + robotId;

            mqttGateway.sendToMqtt(jsonResponse, responseTopic);
            log.info("Sent MQTT response to topic [{}] : {}", responseTopic, jsonResponse);
        } catch (Exception e) {
            log.error("Error processing robot status message : {}", e.getMessage(), e);

            try {
                int robotId = Integer.parseInt(topic.substring(topic.lastIndexOf('/') + 1));
                String errorTopic = "robots/error/" + robotId;
                mqttGateway.sendToMqtt("{\"error\": \"" + e.getMessage() + "\"}", errorTopic);
            } catch (NumberFormatException nfe) {
                log.error("Could not parse robotId from error topic: {}", topic);
            }
        }
    }

    private void processDrugRecognition(String payload) {
        try {
            DrugRecognitionPayload recognitionPayload = objectMapper.readValue(payload, DrugRecognitionPayload.class);
            transportService.updateDrugRecognitionStatus(
                    recognitionPayload.getTransportId(),
                    recognitionPayload.getDrugId()
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to parse drug recognition payload: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing drug recognition update: {}", e.getMessage(), e);
        }
    }
}
