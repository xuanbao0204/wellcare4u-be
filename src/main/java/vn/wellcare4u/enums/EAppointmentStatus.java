package vn.wellcare4u.enums;

public enum EAppointmentStatus {
    PENDING("Đang chờ..."), APPROVED("Đã duyệt"), COMPLETED("Đã hoàn thành"), CANCELLED("Đã hủy");
	
	private final String value;

	EAppointmentStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}