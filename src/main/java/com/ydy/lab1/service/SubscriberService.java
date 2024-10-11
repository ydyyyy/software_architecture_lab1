package com.ydy.lab1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;

@Service
public class SubscriberService {

	private static final String BROKER_URL = "ws://localhost:8080/ws";

	@Value("${subscriber.topic}")
	private String topic;  // 通过外部配置指定订阅主题

	@PostConstruct
	public void subscribeToTopic() {    // 连接到 WebSocket 服务器并订阅主题
		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

		webSocketClient.doHandshake(new TextWebSocketHandler() {
			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				// 连接成功后，发送身份和订阅主题
				// 延迟 500 毫秒后再发送身份和订阅消息，确保 WebSocket 完全就绪

				System.out.println("Connected to broker. Identifying as subscriber for topic: " + topic);
				session.sendMessage(new TextMessage("subscriber"));  // 发送身份信息
				session.sendMessage(new TextMessage("subscribe:" + topic));  // 发送订阅主题
			}

			@Override
			public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
				// 自动处理接收到的消息
				String receivedMessage = message.getPayload();
				System.out.println("Received message on topic [" + topic + "]: " + receivedMessage);
				// 在这里对接收到的消息做进一步处理
			}
		}, BROKER_URL).addCallback(
				result -> System.out.println("Connection successful"),
				ex -> System.out.println("Connection failed")
		);
	}
}
