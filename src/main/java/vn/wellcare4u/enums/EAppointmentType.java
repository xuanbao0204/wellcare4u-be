package vn.wellcare4u.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EAppointmentType {
	EXAMINATION("Khám bệnh"),
	CONSULTATION("Tư vấn"),
	FOLLOW_UP("Tái khám"),
	GENERAL_CHECK_UP("Khám sức khỏe tổng quát"),
	VACCINATION("Tiêm chủng"),
	THERAPY_SESSION("Buổi trị liệu"),
	DIAGNOSTIC_TEST("Xét nghiệm chẩn đoán"),
	RESCHEDULE("Khám lại (do bị hủy)");
	
	private final String label;
	
	public String getLabel() {
		return label;
	}

	EAppointmentType(String label) {
		this.label = label;
	}
	
	public static String toPromptString() {
    	return Arrays.stream(EAppointmentType.values())
    			.map(Enum::name)
    			.collect(Collectors.joining(", "));
    }
}
