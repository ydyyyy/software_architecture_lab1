package com.ydy.lab1.controller;

import com.ydy.lab1.service.MessageBrokerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publish")
public class PublisherController {

	private final MessageBrokerService messageBrokerService;

	public PublisherController(MessageBrokerService messageBrokerService) {
		this.messageBrokerService = messageBrokerService;
	}

	@PostMapping("/{topic}")
	public String publishMessage(@PathVariable String topic, @RequestBody String message) {
		messageBrokerService.publishMessage(topic, message);
		return "Message sent to topic: " + topic;
	}
}
