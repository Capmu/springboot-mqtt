package com.capmu.mqtt.service;

import com.capmu.mqtt.util.JsonUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MqttService {

    @Value("${mqtt.serverUrl}")
    private String mqttServerUrl;

    @Value("${mqtt.topic}")
    private String topic;

    public void sendMqttMessage() throws MqttException {

        IMqttClient client = getMqttClient();
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            MqttMessage msg = buildMqttMessage();
            msg.setQos(0);
            msg.setRetained(true);
            client.publish(topic, msg);
        } catch (MqttException ex) {
            //handle MqttException
        }
    }

    private MqttMessage buildMqttMessage() {
        Map<String, String> sampleMessage = new HashMap<>();
        sampleMessage.put("message", "Sample message");
        sampleMessage.put("createdDate", new Date().toString());

        byte[] payload = JsonUtil.convertMapToJson(sampleMessage).getBytes();
        return new MqttMessage(payload);
    }

    private IMqttClient getMqttClient() throws MqttException {
        String publisherId = UUID.randomUUID().toString(); //using a new client ID for each request is better
        IMqttClient client = new MqttClient(mqttServerUrl, publisherId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName("test");
        options.setPassword("test".toCharArray());

        client.connect(options);

        return client;
    }
}
