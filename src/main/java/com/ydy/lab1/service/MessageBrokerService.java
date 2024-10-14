package com.ydy.lab1.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MessageBrokerService {

	// 主题 -> 订阅者
	private final Map<String, List<WebSocketSession>> topicSubscribers = new ConcurrentHashMap<>();

	// 为每个订阅者维护一个消息队列
	private final Map<WebSocketSession, ConcurrentLinkedQueue<String>> messageQueues = new ConcurrentHashMap<>();


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
					try {
						session.sendMessage(new TextMessage(content));
					} catch (Exception e) {
						// 如果发送失败，保存消息到队列中
						messageQueues.get(session).add(content);
						System.out.println("Message could not be sent, adding to queue for retry.");
					}
				}
			}
		}
	}

	// 定期检查消息队列并尝试重新发送
	@Scheduled(fixedRate = 5000) // 每5秒检查一次
	public void retrySendingMessages() throws Exception {
		System.out.println("Checking message queues for unsent messages...");
		for (Map.Entry<WebSocketSession, ConcurrentLinkedQueue<String>> entry : messageQueues.entrySet()) {
			WebSocketSession session = entry.getKey();
			ConcurrentLinkedQueue<String> queue = entry.getValue();

			while (!queue.isEmpty() && session.isOpen()) {
				String message = queue.poll(); // 从队列中取出消息
				try {
					if (message != null) {
						session.sendMessage(new TextMessage(message));
					}
					System.out.println("Resent message to subscriber: " + message);
				} catch (Exception e) {
					queue.offer(message); // 如果再次失败，将消息放回队列中
					break;
				}
			}
		}
	}

	// 为每个主题添加订阅者
	public void addSubscriber(WebSocketSession session, String topic) {
		topicSubscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(session);
		messageQueues.putIfAbsent(session, new ConcurrentLinkedQueue<>()); // 初始化消息队列
		System.out.println("Subscriber added to topic: " + topic);
	}

	// 订阅者断开连接时，移除其在所有主题中的订阅
	public void removeSubscriber(WebSocketSession session) {
		topicSubscribers.forEach((topic, sessions) -> sessions.remove(session));
		messageQueues.remove(session); // 清除该订阅者的消息队列
		System.out.println("Subscriber removed");
	}
}
