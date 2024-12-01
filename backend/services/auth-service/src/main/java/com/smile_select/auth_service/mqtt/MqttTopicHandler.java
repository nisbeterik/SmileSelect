package com.smile_select.auth_service.mqtt;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Component
public class MqttTopicHandler {

    private final Map<String, CompletableFuture<String>> pendingResponses = new ConcurrentHashMap<>();

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleResponse(
        Message<String> message,
        @Header(MqttHeaders.RECEIVED_TOPIC) String topic
    ) {
        try {
            // Extract correlationId from the topic
            String correlationId = topic.substring(topic.lastIndexOf('/') + 1);

            // Resolve the corresponding pending future
            CompletableFuture<String> future = pendingResponses.remove(correlationId);
            if (future != null) {
                future.complete(message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<String> registerPendingRequest(String correlationId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingResponses.put(correlationId, future);
        return future;
    }
}


