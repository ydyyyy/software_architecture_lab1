package com.ydy.lab1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class MessageBrokerService {

	private final Set<WebSocketSession> subscribers = new HashSet<>();

	public void addSubscriber(WebSocketSession session) {
		subscribers.add(session);
	}

	public void removeSubscriber(WebSocketSession session) {
		subscribers.remove(session);
	}

	public void publishMessage(String topic, String message) {
		TextMessage textMessage = new TextMessage(topic + ": " + message);
		for (WebSocketSession subscriber : subscribers) {
			try {
				subscriber.sendMessage(textMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}