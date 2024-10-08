package com.ydy.lab1.handler;

import com.ydy.lab1.service.MessageBrokerService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

	private final MessageBrokerService messageBrokerService;

	public MessageWebSocketHandler(MessageBrokerService messageBrokerService) {
		this.messageBrokerService = messageBrokerService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 新订阅者加入时
		messageBrokerService.addSubscriber(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// 处理来自客户端的消息（可以扩展）
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// 处理通信错误
		session.close(CloseStatus.SERVER_ERROR);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 订阅者断开连接
		messageBrokerService.removeSubscriber(session);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
