package vn.wellcare4u.models.request;

import lombok.Data;

@Data
public class DoctorListRequest {
	private String keyword;
    private String specialization;
    private String location;
    private Boolean onlyVerified;
}
