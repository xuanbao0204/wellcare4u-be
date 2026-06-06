package vn.wellcare4u.models.ai;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OllamaClient implements AiClient {

	private final WebClient webClient;

	private static final String MODEL = "qwen2.5:3b";
	private static final String OLLAMA_CHAT_URL = "http://localhost:11434/api/chat";

	@Override
	public String chat(String systemPrompt, String userMessage) {

		try {

			Map<String, Object> req = Map.of(
					"model", MODEL, 
					"messages", buildMessages(systemPrompt, userMessage),
					"stream", false);

			Map<?, ?> res = webClient.post()
					.uri(OLLAMA_CHAT_URL)
					.bodyValue(req)
					.retrieve()
					.bodyToMono(Map.class)
					.timeout(Duration.ofSeconds(60))
					.block();

			if (res == null) {
				return "";
			}

			Map<?, ?> message = (Map<?, ?>) res.get("message");

			return message != null ? (String) message.get("content") : "";

		} catch (Exception e) {

			e.printStackTrace();

			return "";
		}
	}

	@Override
	public String prompt(String prompt) {

		return chat(prompt, "Execute the task");
	}

	private List<Map<String, String>> buildMessages(String systemPrompt, String userMessage) {

		if (userMessage == null || userMessage.isBlank()) {

			return List.of(Map.of(
					"role", "system"
					, "content", systemPrompt)
					);
		}

		return List.of(Map.of("role", "system"
				, "content", systemPrompt),
				Map.of(	
					"role", "user"
					, "content", userMessage));
	}
}