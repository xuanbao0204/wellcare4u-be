package vn.wellcare4u.services;

import java.util.Map;

import vn.wellcare4u.entities.User;
import vn.wellcare4u.models.dto.UserDTO;
import vn.wellcare4u.models.request.UserInfoRequest;

public interface UserService {

	UserDTO getUserInfo(Long accountId);

	UserDTO getUserInfoByEmail(String email);

	UserDTO updateUserInfo(String email, UserInfoRequest request);

	Long getIdFromEmail(String email);

	UserDTO convertToDTO(User user);

	Map<Long, String> getUserIds();

}
