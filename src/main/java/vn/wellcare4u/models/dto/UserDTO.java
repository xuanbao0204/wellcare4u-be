package vn.wellcare4u.models.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
	private String email;
	private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String avatar;
    private String role;
}
