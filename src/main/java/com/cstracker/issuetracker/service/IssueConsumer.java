package com.cstracker.issuetracker.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class IssueConsumer {

	@KafkaListener(topics = "cs-issue", groupId = "cs-group")
	public void listen(String message) {
		System.out.println("=============================");
		System.out.println(">>> [Worker] Kafka에서 메시지 도착");
		System.out.println(">>> 내용: " + message);
		System.out.println("=============================");

	}
}
