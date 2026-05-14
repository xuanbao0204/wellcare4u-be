package vn.wellcare4u.enums;

import lombok.Getter;

@Getter
public enum EPostStatus {
	PUBLIC("Công khai"), HIDDEN("Bị ẩn"), CLOSED("Đã đóng");
	
	private final String value;
	
	EPostStatus(String value) {
		this.value = value;
	}
	
	
}
