package vn.wellcare4u.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EModerationAction {
	ALLOW, REVIEW, HIDE, DELETE;

	public static String getPromptString() {
		return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "));
	}
}
