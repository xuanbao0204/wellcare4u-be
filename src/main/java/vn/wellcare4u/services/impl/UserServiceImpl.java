package vn.wellcare4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.User;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.UserDTO;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.services.UserService;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDTO getUserInfo(Long accountId) {
		User user = userRepo.findByAccount_Id(accountId).orElseThrow(() -> new AppException("Không tìm thấy profile tích hợp", "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND));
		return convertToDTO(user);
	}
	
	private UserDTO convertToDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setEmail(user.getAccount().getEmail());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setDob(user.getDob());
		dto.setAvatar(user.getAvatar());
		dto.setGender(user.getGender());
		dto.setRole(user.getAccount().getRole().name());
		return dto;
	}

	@Override
	public Object getUserInfoByEmail(String email) {
		User user = userRepo.findByAccount_Email(email).orElseThrow(() -> new AppException("Không tìm thấy profile tích hợp", "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND));
		return convertToDTO(user);
	}
}
