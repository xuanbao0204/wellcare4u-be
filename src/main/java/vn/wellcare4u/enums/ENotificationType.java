package vn.wellcare4u.enums;

import lombok.Getter;

@Getter

public enum ENotificationType {

	INFO("Thông tin"), 
	WARNING ("Cảnh báo"), 
	SYSTEM ("Hệ thống");
	
	private final String value;
	
	private ENotificationType(String value) {
		this.value = value;
	}
}
