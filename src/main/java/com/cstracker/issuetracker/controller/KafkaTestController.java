package com.cstracker.issuetracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor // 생성자 주입 자동화
public class KafkaTestController {

	// KafkaTemplate: DB의 JpaRepository 처럼 Kafka에 데이터를 보내는 도구
	private final KafkaTemplate<String, String> kafkaTemplate;

	@GetMapping("/kafka/test")
	public String sendMessage(@RequestParam("msg") String message) {
		// "cs-issue" 라는 토픽(Topic)으로 message 보냄
		kafkaTemplate.send("cs-issue", message);

		return "Kafka로 메시지 전송 성공: " + message;
	}
}
