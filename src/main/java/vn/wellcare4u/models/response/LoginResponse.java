package vn.wellcare4u.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.UserDTO;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {

	private String accessToken;
	private String refreshToken;
	private String accStatus;
	private UserDTO user;
}
