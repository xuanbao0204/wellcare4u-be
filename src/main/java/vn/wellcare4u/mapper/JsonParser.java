package vn.wellcare4u.mapper;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class JsonParser {

	private static final ObjectMapper mapper = new ObjectMapper();

	private JsonParser() {
	}

	public static <T> T parse(String rawJson, Class<T> clazz) {
		try {
			log.info(rawJson);
			if (rawJson == null || rawJson.isBlank()) {
				throw new RuntimeException("AI response is empty");
			}

			String cleaned = rawJson.replace("```json", "").replace("```", "").trim();

			JsonNode node = mapper.readTree(cleaned);

			/*
			 * AI trả: "{ \"field\":\"value\" }" -> JSON string -> unwrap
			 */
			if (node.isString()) {
				cleaned = node.stringValue();
			}

			return mapper.readValue(cleaned, clazz);

		} catch (Exception e) {
			throw new RuntimeException("Failed to parse AI response: " + rawJson, e);
		}
	}
	
	public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static String toJson(Object obj) {
	    try {
	        return mapper.writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to serialize object to JSON", e);
	    }
	}
}