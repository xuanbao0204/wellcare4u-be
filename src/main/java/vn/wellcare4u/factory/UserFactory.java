package vn.wellcare4u.factory;

import org.springframework.stereotype.Component;

import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.admin.Admin;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.ERole;

@Component
public class UserFactory {

    public User createUser(ERole role) {

        return switch (role) {
            case DOCTOR -> new Doctor();
            case PATIENT -> new Patient();
            case ADMIN -> new Admin();
            default -> throw new IllegalArgumentException("Invalid role");
        };
    }
}