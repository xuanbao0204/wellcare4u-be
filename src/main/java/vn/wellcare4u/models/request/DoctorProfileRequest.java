package vn.wellcare4u.models.request;

import lombok.Data;

@Data
public class DoctorProfileRequest {
    private String bio;
    private String certification;
    private String specialization;
    private Integer experienceYears;
    private Double consultationFee;
    private String clinicAddress;
}