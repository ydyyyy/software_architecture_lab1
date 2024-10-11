package com.ydy.lab1.publisher;

import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketPublisher {

	private WebSocketSession session;

	public void connect() throws Exception {
		StandardWebSocketClient client = new StandardWebSocketClient();
		session = client.doHandshake(new TextWebSocketHandler(), "ws://localhost:8080/ws").get();
		session.sendMessage(new TextMessage("publisher"));  // 发送身份声明
	}

	public void publish(String topic, String message) throws Exception {
		String payload = topic + ":" + message;
		if (session.isOpen()) {
			session.sendMessage(new TextMessage(payload));
			System.out.println("Published message: " + message);
		} else {
			System.out.println("WebSocket session is closed.");
		}
	}

	public void close() throws Exception {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

}
