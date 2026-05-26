package vn.wellcare4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.Account;
import vn.wellcare4u.enums.EAccountStatus;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.services.AccountService;
import vn.wellcare4u.services.RefreshTokenService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accRepo;

	@Autowired
	private PasswordEncoder pwEncoder;
	
	@Autowired
    private RefreshTokenService refreshTokenService;

	@Override
	public void activeAccount(Long id) {
		Account acc = accRepo.findById(id).orElseThrow(
				() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));


		acc.setStatus(EAccountStatus.ACTIVE);
		accRepo.save(acc);
	}

	@Override
	public void lockAccount(Long id) {
		Account acc = accRepo.findById(id).orElseThrow(
				() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));

		acc.setStatus(EAccountStatus.LOCKED);
		accRepo.save(acc);
	}

	@Override
	public void deActiveAccount(Long id) {
		Account acc = accRepo.findById(id).orElseThrow(
				() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));

		acc.setStatus(EAccountStatus.INACTIVE);
		accRepo.save(acc);
	}

	@Override
	public void deleteAccount(Long id) {
		Account acc = accRepo.findById(id).orElseThrow(
				() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));

		acc.setStatus(EAccountStatus.DELETED);
		accRepo.save(acc);
	}

	@Override
	public void changePassword(String currentPassword, String newPassword, String email) {

		Account acc = accRepo.findByEmail(email).orElseThrow(
				() -> new AppException("Tài khoản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));

		if (!pwEncoder.matches(currentPassword, acc.getPassword())) {
			throw new AppException("Mật khẩu hiện tại không đúng", "INVALID_CURRENT_PASSWORD", HttpStatus.BAD_REQUEST);
		}

		if (pwEncoder.matches(newPassword, acc.getPassword())) {
			throw new AppException("Mật khẩu mới không được trùng mật khẩu cũ", "DUPLICATE_PASSWORD",
					HttpStatus.BAD_REQUEST);
		}

		acc.setPassword(pwEncoder.encode(newPassword));

		accRepo.save(acc);
	}
	
	@Override
	public void resetPassword(String email, String newPassword) {

	    Account acc = accRepo.findByEmail(email)
	            .orElseThrow(() -> new AppException(
	                    "Tài khoản không tồn tại",
	                    "ACCOUNT_NOT_FOUND",
	                    HttpStatus.NOT_FOUND
	            ));
	    if (pwEncoder.matches(newPassword, acc.getPassword())) {
	        throw new AppException(
	                "Mật khẩu mới không được trùng mật khẩu cũ",
	                "DUPLICATE_PASSWORD",
	                HttpStatus.BAD_REQUEST
	        );
	    }

	    acc.setPassword(pwEncoder.encode(newPassword));

	    accRepo.save(acc);

	    refreshTokenService.deleteByAccount(acc);
	}
	
	@Override
	public String getAccountStatus(String email) {
		Account acc = accRepo.findByEmail(email).orElseThrow(
				() -> new AppException("Tài khoản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		return acc.getStatus().name();
	}
	
	@Override
	public void activateAccount(String email) {
		Account acc = accRepo.findByEmail(email).orElseThrow(
				() -> new AppException("Tài khoản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		acc.setStatus(EAccountStatus.ACTIVE);
		accRepo.save(acc);
	}
	
	@Override
	public void deactivateAccount(String email) {
		Account acc = accRepo.findByEmail(email).orElseThrow(
				() -> new AppException("Tài khoản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		acc.setStatus(EAccountStatus.INACTIVE);
		accRepo.save(acc);
	}
	
	@Override
	public void deleteAccount(String email, String password) {
		Account acc = accRepo.findByEmail(email).orElseThrow(
				() -> new AppException("Tài khoản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		if (!pwEncoder.matches(password, acc.getPassword())) {
			throw new AppException("Mật khẩu hiện tại không đúng", "INVALID_CURRENT_PASSWORD", HttpStatus.BAD_REQUEST);
		}
		
		acc.setStatus(EAccountStatus.DELETED);
		accRepo.save(acc);
	}
}
