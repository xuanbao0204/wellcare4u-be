package vn.wellcare4u.models.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAccountDTO {

    private Long id;
    private Long userId;

    private String email;
    private String role;
    private String status;

    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private String avatar;
    private String dob;

    // Chỉ có khi role = DOCTOR
    private Boolean verified;
    private String specialization;
    private Integer experienceYears;
    private String clinicAddress;
    private Double consultationFee;
    private String certification;

    private String createdAt;
    private String lastLoginAt;
}