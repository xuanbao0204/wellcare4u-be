package vn.wellcare4u.services;

import java.util.Map;

import vn.wellcare4u.models.dto.PatientDTO;
import vn.wellcare4u.models.dto.patient.PatientDashboardDTO;
import vn.wellcare4u.models.request.PatientProfileRequest;

public interface PatientService {

	PatientDTO updatePatientProfile(String email, PatientProfileRequest req);

	PatientDTO getPatientProfile(String email);

	PatientDashboardDTO getDashboard(String email);

	Map<Long, String> getPatientIdsByDoctor(Long doctorId);

}
