package vn.wellcare4u.models.request;

import lombok.Data;

@Data
public class PatientProfileRequest {

	private String emergencyContact;
    private String bloodType;
    private String insuranceNumber;
    private String insuranceImage;
}
