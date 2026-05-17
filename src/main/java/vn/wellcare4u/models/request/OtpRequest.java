package vn.wellcare4u.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.enums.EOTPType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {
    private String email;
    private String code;
    private EOTPType type;
    
    public OtpRequest(String email, EOTPType type) {
    	this.email = email;
    	this.type = type;
    }
}