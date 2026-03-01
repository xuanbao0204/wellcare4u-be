package vn.wellcare4u.enums;

public enum ERole {
    ADMIN("Quản trị viên"), DOCTOR("Bác sỹ"), PATIENT("Người dùng"), STAFF("Nhân viên");
    
    private final String roleName;
	
	ERole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}