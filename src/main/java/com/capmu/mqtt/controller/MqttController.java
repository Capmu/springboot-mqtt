package com.capmu.mqtt.controller;

import com.capmu.mqtt.service.MqttService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MqttController {

    private final MqttService mqttService;

    @PostMapping("/message")
    public ResponseEntity<String> sendMqttMessage() throws MqttException {
        mqttService.sendMqttMessage();
        return ResponseEntity.ok("MQTT message has sent!");
    }
}
