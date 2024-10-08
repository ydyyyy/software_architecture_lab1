package com.ydy.lab1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.ydy.lab1.client.Publisher.publish;

@SpringBootTest
class Lab1ApplicationTests {

	@Test
	void contextLoads() {
		String topic = "news";
		String message = "Breaking news: Java WebSocket is amazing!";
		publish(topic, message);
	}

}
