package com.ydy.lab1.handler;

import com.ydy.lab1.service.MessageBrokerService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

	private final MessageBrokerService messageBrokerService;
	private final Map<WebSocketSession, String> sessionRoles = new ConcurrentHashMap<>(); // 追踪角色 (发布者或订阅者)

	public MessageWebSocketHandler(MessageBrokerService messageBrokerService) {
		this.messageBrokerService = messageBrokerService;
	}

	// WebSocket 连接建立时调用
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 暂时不区分，等待第一次消息来确认角色
		System.out.println("New WebSocket connection established");
	}

	// 处理接收到的消息
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		String payload = message.getPayload().toString();

		// 首次消息：判断角色为发布者或订阅者
		if (!sessionRoles.containsKey(session)) {
			if ("subscriber".equalsIgnoreCase(payload)) {
				sessionRoles.put(session, "subscriber");
				System.out.println("Session identified as subscriber.");
			} else if ("publisher".equalsIgnoreCase(payload)) {
				sessionRoles.put(session, "publisher");
				System.out.println("Session identified as publisher.");
			} else {
				session.sendMessage(new TextMessage("Invalid role: Please send 'publisher' or 'subscriber' as the first message."));
				session.close(CloseStatus.BAD_DATA);
			}
			return;
		}

		// 处理订阅者的订阅请求
		if (payload.startsWith("subscribe:") && "subscriber".equalsIgnoreCase(sessionRoles.get(session))) {
			String topic = payload.split(":")[1];
			messageBrokerService.addSubscriber(session, topic); // 绑定订阅者和主题
			System.out.println("Subscriber subscribed to topic: " + topic);
		}

		// 处理发布者的消息
		if ("publisher".equalsIgnoreCase(sessionRoles.get(session))) {

			messageBrokerService.processMessage(payload); // 将消息转发给订阅者
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// 处理通信错误
		session.close(CloseStatus.SERVER_ERROR);
	}

	// WebSocket 连接关闭时调用
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 如果订阅者断开连接，移除它
		if ("subscriber".equalsIgnoreCase(sessionRoles.get(session))) {
			messageBrokerService.removeSubscriber(session);
		}
		sessionRoles.remove(session);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
