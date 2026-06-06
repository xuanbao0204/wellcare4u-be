package vn.wellcare4u.models.ai;

public interface AiClient {

	String chat(String systemPrompt, String userMessage);

	String prompt(String systemPrompt);
}