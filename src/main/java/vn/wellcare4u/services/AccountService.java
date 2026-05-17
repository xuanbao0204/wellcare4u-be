package vn.wellcare4u.services;

public interface AccountService {

	void deleteAccount(Long id);

	void deActiveAccount(Long id);

	void lockAccount(Long id);

	void activeAccount(Long id);

	void activateAccount(String email);

	String getAccountStatus(String email);

	void forgotPassword(String email);

	void changePassword(String currentPassword, String newPassword, String email);

	void deleteAccount(String email, String password);

	void deactivateAccount(String email);

}
