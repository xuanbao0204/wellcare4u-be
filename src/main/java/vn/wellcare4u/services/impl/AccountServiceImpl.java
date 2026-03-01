package vn.wellcare4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.Account;
import vn.wellcare4u.enums.EAccountStatus;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.services.AccountService;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountRepository accRepo;
	
	@Override
	public void activeAccount(Long id) {
		Account acc = accRepo.findById(id)
				.orElseThrow(() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		acc.setStatus(EAccountStatus.ACTIVE);
		accRepo.save(acc);
	}
	
	@Override
	public void lockAccount(Long id) {
		Account acc = accRepo.findById(id)
				.orElseThrow(() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		acc.setStatus(EAccountStatus.LOCKED);
		accRepo.save(acc);
	}
	
	@Override
	public void deActiveAccount(Long id) {
		Account acc = accRepo.findById(id)
				.orElseThrow(() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		acc.setStatus(EAccountStatus.INACTIVE);
		accRepo.save(acc);
	}
	
	@Override
	public void deleteAccount(Long id) {
		Account acc = accRepo.findById(id)
				.orElseThrow(() -> new AppException("Tài khooản không tồn tại", "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		acc.setStatus(EAccountStatus.DELETED);
		accRepo.save(acc);
	}
}
