package vn.wellcare4u.enums;

public enum EAccountStatus {
    ACTIVE("Đã kích hoạt"), INACTIVE("Chưa kích hoạt"), LOCKED("Bị khóa"), DELETED("Đã xóa");
    
    private final String statusValue;
	
	public String getStatusValue() {
		return statusValue;
	}
	
	EAccountStatus(String statusValue) {
		this.statusValue = statusValue;
	}
}