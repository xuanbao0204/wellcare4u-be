package vn.wellcare4u.enums;

import lombok.Getter;

@Getter
public enum ERecordStatus {
	DRAFT("Bản nháp"),
    FINALIZED("Đã hoàn thiện"),;

	private final String value;

	ERecordStatus(String value) {
		this.value = value;
	}
    
    
}
