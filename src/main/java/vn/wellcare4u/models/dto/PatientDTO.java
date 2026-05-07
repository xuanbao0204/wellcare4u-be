package vn.wellcare4u.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientDTO {
	private Long id;
	private String email;
	private String firstName;
    private String lastName;
    private String gender;
    private String avatar;
    
	private String emergencyContact;
    private String bloodType;
    private String insuranceNumber;
    private String insuranceImage;
}
