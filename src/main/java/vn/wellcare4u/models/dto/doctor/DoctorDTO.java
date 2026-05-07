package vn.wellcare4u.models.dto.doctor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorDTO {
	private Long id;
	private String email;
	private String firstName;
    private String lastName;
    private String gender;
    private String avatar;
    
    private String bio;
    private String certification;
    private String specialization;
    private Integer experienceYears;
    private Double consultationFee;
    private String clinicAddress;
    boolean verified;
}