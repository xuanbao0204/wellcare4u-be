package vn.wellcare4u.enums;

public enum ETimeSlotStatus {
    AVAILABLE("Còn trống"), BOOKED("Đã đặt"), BLOCKED("Đã khóa");
    
    private final String displayName;
    
    public String getDisplayName() {
		return displayName;
	}
	
	ETimeSlotStatus(String displayName) {
		this.displayName = displayName;
	}
}
