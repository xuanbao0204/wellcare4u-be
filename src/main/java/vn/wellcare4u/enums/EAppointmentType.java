package vn.wellcare4u.enums;

public enum EAppointmentType {
	CONSULTATION("Tư vấn"),
	FOLLOW_UP("Tái khám"),
	CHECK_UP("Khám sức khỏe định kỳ"),
	VACCINATION("Tiêm chủng"),
	THERAPY_SESSION("Buổi trị liệu"),
	DIAGNOSTIC_TEST("Xét nghiệm chẩn đoán"),;
	
	private final String label;
	
	public String getLabel() {
		return label;
	}

	EAppointmentType(String label) {
		this.label = label;
	}
	
	
}
