package com.cstracker.issuetracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class IssueConsumer {

	// Spring AI가 제공하는 임베딩 해주는 도구
	private final EmbeddingModel embeddingModel;

	@KafkaListener(topics = "cs-issue", groupId = "cs-group")
	public void listen(String message) {
		System.out.println("=============================");
		System.out.println(">>> [Worker] Kafka에서 메시지 도착: " + message);

		// AI에게 텍스트를 주고 벡터로 바꿔달라고 함
		float[] vector = embeddingModel.embed(message);

		System.out.println(">>> [AI] 임베딩 변환 완료 (차원 수: " + vector.length + ")");
		if (vector.length > 5) {
			float[] preview = Arrays.copyOfRange(vector, 0, 5);
			System.out.println(">>> 벡터 데이터(앞 5개): " + Arrays.toString(preview));
		} else {
			System.out.println(">>> 벡터 데이터: " + Arrays.toString(vector));
		}
		System.out.println("=============================");

	}
}
