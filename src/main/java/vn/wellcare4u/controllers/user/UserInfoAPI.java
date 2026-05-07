package vn.wellcare4u.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.UserInfoRequest;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserInfoAPI {

	@Autowired
	UserService userServ;
	
	@GetMapping("")
    public ApiResponse<?> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.builder()
            		.status(401)
            		.errorCode("NOT_AUTHORIZED")
            		.message("Unauthorized access")
            		.build();
        }

        String email = authentication.getName();

        return ApiResponse.builder()
        		.status(200)
        		.message("User information retrieved successfully")
        		.data(userServ.getUserInfoByEmail(email))
        		.build();
    }
	
	@PutMapping("")
	public ApiResponse<?> updateUser(
	        Authentication authentication,
	        @RequestBody UserInfoRequest request
	) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ApiResponse.builder()
	                .status(401)
	                .errorCode("NOT_AUTHORIZED")
	                .message("Unauthorized access")
	                .build();
	    }

	    String email = authentication.getName();

	    return ApiResponse.builder()
	            .status(200)
	            .message("Updated successfully")
	            .data(userServ.updateUserInfo(email, request))
	            .build();
	}
}
