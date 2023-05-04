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

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    public void sendMqttMessage() throws MqttException {

        IMqttClient client = getMqttClient();
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            MqttMessage msg = buildMqttMessage();

            // 0 – “at most once” semantics, also known as “fire-and-forget”. Use this option when message loss is acceptable, as it does not require any kind of acknowledgment or persistence
            // 1 – “at least once” semantics. Use this option when message loss is not acceptable and your subscribers can handle duplicates
            // 2 – “exactly once” semantics. Use this option when message loss is not acceptable and your subscribers cannot handle duplicates
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

    public IMqttClient getMqttClient() throws MqttException {
        String publisherId = UUID.randomUUID().toString(); //using a new client ID for each request is better
        IMqttClient client = new MqttClient(mqttServerUrl, publisherId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        client.connect(options);

        return client;
    }
}
