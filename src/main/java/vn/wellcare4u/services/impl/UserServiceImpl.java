package vn.wellcare4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.User;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.UserDTO;
import vn.wellcare4u.models.request.UserInfoRequest;
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
		dto.setId(user.getId());
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
	public UserDTO getUserInfoByEmail(String email) {
		User user = userRepo.findByAccount_Email(email).orElseThrow(() -> new AppException("Không tìm thấy profile tích hợp", "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND));
		return convertToDTO(user);
	}
	
	@Override
	public UserDTO updateUserInfo(String email, UserInfoRequest request) {
	    User user = userRepo.findByAccount_Email(email)
	            .orElseThrow(() -> new AppException(
	                    "Không tìm thấy profile",
	                    "PROFILE_NOT_FOUND",
	                    HttpStatus.NOT_FOUND
	            ));

	    if (request.getFirstName() != null)
	        user.setFirstName(request.getFirstName());

	    if (request.getLastName() != null)
	        user.setLastName(request.getLastName());

	    if (request.getDob() != null)
	        user.setDob(request.getDob());

	    if (request.getGender() != null)
	        user.setGender(request.getGender());

	    if (request.getPhone() != null)
	        user.setPhone(request.getPhone());

	    if (request.getAddress() != null)
	        user.setAddress(request.getAddress());
	    
	    if (request.getAvatar() != null)
	        user.setAvatar(request.getAvatar());

	    userRepo.save(user);

	    return convertToDTO(user);
	}
	
	@Override
	public Long getIdFromEmail(String email) {
		User u = userRepo.findByAccount_Email(email).orElseThrow(() -> new AppException("Không tìm thấy profile tích hợp", "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND));
		System.out.println("User from service: " + u.getId());
		return u.getId();
	}
}
