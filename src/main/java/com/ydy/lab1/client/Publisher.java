package com.ydy.lab1.client;

import org.springframework.web.client.RestTemplate;

public class Publisher {

	private static final String BROKER_URL = "http://localhost:8080/publish/";

	public static void publish(String topic, String message) {
		RestTemplate restTemplate = new RestTemplate();
		String url = BROKER_URL + topic;
		restTemplate.postForObject(url, message, String.class);
		System.out.println("Published message: " + message + " to topic: " + topic);
	}

}
