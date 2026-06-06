package vn.wellcare4u.services;

import vn.wellcare4u.entities.User;
import vn.wellcare4u.models.dto.ProfileCompletionResult;

public interface ProfileCompletionService {

	ProfileCompletionResult calculate(User user);

}
