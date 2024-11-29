package com.smile_select.auth_service.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {

	// Use this in service class in order to publish messages via MQTT
	void publishMessage(String data, @Header(MqttHeaders.TOPIC) String topic);
}
