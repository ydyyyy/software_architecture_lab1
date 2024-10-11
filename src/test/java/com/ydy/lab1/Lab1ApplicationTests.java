package com.ydy.lab1;

import com.ydy.lab1.publisher.WebSocketPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Lab1ApplicationTests {

	@Test
	void contextLoads() throws Exception {
		WebSocketPublisher publisher = new WebSocketPublisher();
		publisher.connect();

		// 模拟发送大量消息
		for (int i = 0; i < 10; i++) {
			publisher.publish("news", "Message " + i);
			Thread.sleep(100);  // 模拟每秒发布10条消息，实际中可调节发送频率
		}
		publisher.close();
	}

}
