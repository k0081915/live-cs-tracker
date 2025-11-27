package com.cstracker.issuetracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IssueConsumer {

	// EmbeddingModel 대신 사용
	// 알아서 임베딩도 하고, Redis 저장도 하고 검색도 함
	private final VectorStore vectorStore;

	@KafkaListener(topics = "cs-issue", groupId = "cs-group")
	public void listen(String message) {
		System.out.println("=============================");
		System.out.println(">>> [Worker] 새 이슈 도착: " + message);

		// 1. 유사한 이슈가 있는지 먼저 검색 (RAG 핵심)
		// - 쿼리: 방금 들어온 메시지
		// - 조건: 유사도 0.8 이상인 것 상위 3개만 가져오기
		List<Document> similarIssues = vectorStore.similaritySearch(
				SearchRequest.builder().topK(3).similarityThreshold(0.8).query(message).build()
		);

		if (similarIssues.isEmpty()) {
			System.out.println(">>> 🔵 새로운 유형의 이슈입니다 (유사한 건 없음)");
		} else {
			System.out.println(">>> 🔴 이미 비슷한 이슈가 " + similarIssues.size() + "건 있습니다");
			for (Document doc : similarIssues) {
				System.out.println("	- 기존 이슈 내용: " + doc.getText());
			}
		}

		// 2. 검색이 끝났으니 message도 Redis에 저장시킴
		// Document 객체로 감싸서 저장(메타데이터 추가 가능)
		Document newDoc = new Document(message, Map.of("source", "kafka", "timestamp", System.currentTimeMillis()));
		vectorStore.add(List.of(newDoc));

		System.out.println(">>> [DB] Redis 저장 완료");
		System.out.println("=============================");

	}
}
