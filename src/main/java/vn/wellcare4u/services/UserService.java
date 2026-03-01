package vn.wellcare4u.services;

import vn.wellcare4u.models.dto.UserDTO;

public interface UserService {

	UserDTO getUserInfo(Long accountId);

	Object getUserInfoByEmail(String email);

}
