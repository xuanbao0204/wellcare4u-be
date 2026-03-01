package vn.wellcare4u.services;

public interface AccountService {

	void deleteAccount(Long id);

	void deActiveAccount(Long id);

	void lockAccount(Long id);

	void activeAccount(Long id);

}
