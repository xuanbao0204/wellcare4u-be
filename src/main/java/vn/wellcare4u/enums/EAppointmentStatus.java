package vn.wellcare4u.enums;

public enum EAppointmentStatus {
    PENDING("Đang chờ..."), CONFIRMED("Đã xác nhận"), IN_PROGRESS("Đang tiến hành"), COMPLETED("Đã hoàn thành"), CANCELLED("Đã hủy"), EXPIRED("Quá hạn"), CHECKED_IN("Bệnh nhân đã tới");
	
	private final String value;

	EAppointmentStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}