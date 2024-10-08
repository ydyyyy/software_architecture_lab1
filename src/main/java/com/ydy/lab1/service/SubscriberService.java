package com.ydy.lab1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;

@Service
public class SubscriberService {

	private static final String BROKER_URL = "ws://localhost:8080/ws";

	@Value("${subscriber.topic}")
	private String topic;  // 可以动态配置

	@PostConstruct
	public void subscribeToTopic() {
		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

		webSocketClient.doHandshake(new TextWebSocketHandler() {
			@Override
			public void handleTextMessage(org.springframework.web.socket.WebSocketSession session, TextMessage message) throws Exception {
				// 自动处理接收到的消息
				String receivedMessage = message.getPayload();
				if (receivedMessage.startsWith(topic)) {
					System.out.println("Received message on topic [" + topic + "]: " + receivedMessage);
					// 在这里对接收到的消息做进一步处理
				}
			}

			@Override
			public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
				System.out.println("Connected to broker and listening on topic: " + topic);
			}
		}, BROKER_URL).addCallback(
				result -> System.out.println("Connection successful"),
				ex -> System.out.println("Connection failed")
		);
	}
}