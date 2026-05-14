package vn.wellcare4u.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.UserDTO;
import vn.wellcare4u.models.dto.doctor.DoctorDTO;
import vn.wellcare4u.models.request.DoctorListRequest;
import vn.wellcare4u.models.request.DoctorProfileRequest;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.services.DoctorService;
import vn.wellcare4u.services.UserService;

@Service
public class DoctorServiceImpl implements DoctorService{

	@Autowired
	private DoctorRepository doctorRepo;
	
	@Autowired
	private UserService uServ;
	
	@Override
	public List<DoctorDTO> findAllDoctor(){
		return doctorRepo.findAll().stream().map(this::convertToFullDTO).toList();
	}
	
	@Override
	public Page<DoctorDTO> findAllDoctorPage(Pageable pageable, DoctorListRequest req ){
		
		 Page<Doctor> page = doctorRepo.searchDoctors(
	                req.getKeyword(),
	                req.getSpecialization(),
	                req.getLocation(),
	                req.getOnlyVerified(),
	                pageable
	        );
		 
	    return page.map(this::convertToFullDTO);

	}
	
	@Override
	public DoctorDTO findDoctorById(Long doctorId) {
		Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new AppException(
                "Doctor not found",
                "DOCTOR_NOT_FOUND",
                HttpStatus.NOT_FOUND
        ));
		return convertToFullDTO(doctor);
	}
	
	
	@Override
	public DoctorDTO getDoctorProfile(String email) {
	    Doctor doctor = doctorRepo.findByAccount_Email(email).orElseThrow(() -> new AppException(
	                    "Doctor not found",
	                    "DOCTOR_NOT_FOUND",
	                    HttpStatus.NOT_FOUND
	            ));
	    return convertToDTO(doctor);
	}
	
	@Override
	public DoctorDTO updateDoctorProfile(String email, DoctorProfileRequest request) {

	    Doctor doctor = doctorRepo.findByAccount_Email(email).orElseThrow(() -> new AppException(
	                    "Doctor not found",
	                    "DOCTOR_NOT_FOUND",
	                    HttpStatus.NOT_FOUND
	            ));

	    if (request.getBio() != null)
	        doctor.setBio(request.getBio());

	    if (request.getCertification() != null)
	        doctor.setCertification(request.getCertification());

	    if (request.getSpecialization() != null)
	        doctor.setSpecialization(ESpecialization.valueOf(request.getSpecialization()));

	    if (request.getExperienceYears() != null)
	        doctor.setExperienceYears(request.getExperienceYears());

	    if (request.getConsultationFee() != null)
	        doctor.setConsultationFee(request.getConsultationFee());

	    if (request.getClinicAddress() != null)
	        doctor.setClinicAddress(request.getClinicAddress());

	    doctorRepo.save(doctor);

	    return convertToDTO(doctor);
	}
	
	private DoctorDTO convertToFullDTO(Doctor doctor) {
		
		UserDTO u = uServ.getUserInfoByEmail(doctor.getAccount().getEmail());
		
		return DoctorDTO.builder()
				.id(doctor.getId())
				.avatar(u.getAvatar())
				.email(u.getEmail())
				.firstName(u.getFirstName())
				.lastName(u.getLastName())
				.gender(u.getGender())
				
				.bio(doctor.getBio())
	            .certification(doctor.getCertification())
	            .specialization(doctor.getSpecialization().getDisplayName())
	            .experienceYears(doctor.getExperienceYears())
	            .consultationFee(doctor.getConsultationFee())
	            .clinicAddress(doctor.getClinicAddress())
	            .verified(doctor.isVerified())
	            .build();
	}
	
	private DoctorDTO convertToDTO(Doctor doctor) {
	    return DoctorDTO.builder()
	            .bio(doctor.getBio())
	            .certification(doctor.getCertification())
	            .specialization(doctor.getSpecialization().getDisplayName())
	            .experienceYears(doctor.getExperienceYears())
	            .consultationFee(doctor.getConsultationFee())
	            .clinicAddress(doctor.getClinicAddress())
	            .verified(doctor.isVerified())
	            .build();
	}
}
