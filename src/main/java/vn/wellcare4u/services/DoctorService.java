package vn.wellcare4u.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.wellcare4u.models.dto.doctor.DoctorDTO;
import vn.wellcare4u.models.request.DoctorListRequest;
import vn.wellcare4u.models.request.DoctorProfileRequest;

public interface DoctorService {

	DoctorDTO updateDoctorProfile(String email, DoctorProfileRequest request);

	DoctorDTO getDoctorProfile(String email);

	List<DoctorDTO> findAllDoctor();

	Page<DoctorDTO> findAllDoctorPage(Pageable pageable, DoctorListRequest req);

}
