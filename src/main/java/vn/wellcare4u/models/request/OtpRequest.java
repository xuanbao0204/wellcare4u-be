package vn.wellcare4u.models.request;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String code;
}