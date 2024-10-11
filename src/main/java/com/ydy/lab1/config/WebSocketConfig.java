package com.ydy.lab1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.ydy.lab1.handler.MessageWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {   // 配置 WebSocket

	private final MessageWebSocketHandler webSocketHandler;

	// 注入 MessageWebSocketHandler
	public WebSocketConfig(MessageWebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	// 注册 WebSocket 处理器
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");
	}
}