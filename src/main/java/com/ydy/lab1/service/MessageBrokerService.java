package com.ydy.lab1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageBrokerService {

	// 主题 -> 订阅者
	private final Map<String, List<WebSocketSession>> topicSubscribers = new ConcurrentHashMap<>();

	// 处理发布者的消息并转发给相应主题的订阅者
	public void processMessage(String message) throws Exception {
		// 假设消息格式为 "topic:content"
		String[] parts = message.split(":", 2);
		if (parts.length < 2) return;
		String topic = parts[0];
		String content = parts[1];

		// 获取订阅了该主题的所有订阅者并推送消息
		List<WebSocketSession> sessions = topicSubscribers.get(topic);
		if (sessions != null) {
			for (WebSocketSession session : sessions) {
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(content));
				}
			}
		}
	}

	// 订阅者请求订阅主题
	public void addSubscriber(WebSocketSession session, String topic) {
		topicSubscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(session);
		System.out.println("Subscriber added to topic: " + topic);
	}

	// 订阅者断开连接时，移除其在所有主题中的订阅
	public void removeSubscriber(WebSocketSession session) {
		topicSubscribers.forEach((topic, sessions) -> sessions.remove(session));
		System.out.println("Subscriber removed from all topics");
	}
}
