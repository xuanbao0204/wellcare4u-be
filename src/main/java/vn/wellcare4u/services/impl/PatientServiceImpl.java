package vn.wellcare4u.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.PatientDTO;
import vn.wellcare4u.models.dto.UserDTO;
import vn.wellcare4u.models.request.PatientProfileRequest;
import vn.wellcare4u.repositories.PatientRepository;
import vn.wellcare4u.services.PatientService;
import vn.wellcare4u.services.UserService;

@Service
public class PatientServiceImpl implements PatientService {

	@Autowired
	private PatientRepository patientRepo;
	
	@Autowired
	private UserService uServ;

	@Override
	public PatientDTO getPatientProfile(String email) {
		Patient patient = patientRepo.findByAccount_Email(email)
				.orElseThrow(() -> new AppException("Patient not found", "PATIENT_NOT_FOUND", HttpStatus.NOT_FOUND));
		return mapToDTO(patient);
	}
	
	@Override
	public PatientDTO updatePatientProfile(String email, PatientProfileRequest req) {
		Patient patient = patientRepo.findByAccount_Email(email)
				.orElseThrow(() -> new AppException("Patient not found", "PATIENT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		if (req.getBloodType() != null) patient.setBloodType(req.getBloodType());
		if (req.getEmergencyContact() != null) patient.setEmergencyContact(req.getEmergencyContact());
		if (req.getInsuranceImage() != null) patient.setInsuranceImage(req.getInsuranceImage());
		if (req.getInsuranceNumber() != null) patient.setInsuranceNumber(req.getInsuranceNumber());
		
		patientRepo.save(patient);
		return mapToDTO(patient);
	}
	
	private PatientDTO mapToFullDTO(Patient p) {
		
		UserDTO u = uServ.getUserInfoByEmail(p.getAccount().getEmail());
		return PatientDTO.builder()
				.id(p.getId())
				.avatar(u.getAvatar())
				.email(u.getEmail())
				.firstName(p.getFirstName())
				.lastName(p.getLastName())
				.gender(p.getGender())
				
				.emergencyContact(p.getEmergencyContact())
				.bloodType(p.getBloodType())
				.insuranceNumber(p.getInsuranceNumber())
				.insuranceImage(p.getInsuranceImage())
				.build();
	}
	
	private PatientDTO mapToDTO(Patient p) {
		return PatientDTO.builder()
				.emergencyContact(p.getEmergencyContact())
				.bloodType(p.getBloodType())
				.insuranceNumber(p.getInsuranceNumber())
				.insuranceImage(p.getInsuranceImage())
				.build();
	}
}
