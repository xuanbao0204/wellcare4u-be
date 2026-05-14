package vn.wellcare4u.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

	
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;
	@NotBlank(message = "Password không được để trống")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Password phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    private String password;
	private String role;
	private String firstName;
	private String lastName;
}
