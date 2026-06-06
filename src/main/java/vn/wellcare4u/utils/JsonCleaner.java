package vn.wellcare4u.utils;

public class JsonCleaner {

	public static String clean(String text) {

		if (text == null) {
			return null;
		}

		return text.replace("```json", "").replace("```", "").trim();
	}
}