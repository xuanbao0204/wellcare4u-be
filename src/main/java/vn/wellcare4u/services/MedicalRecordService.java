package vn.wellcare4u.services;

import java.util.List;

import vn.wellcare4u.models.dto.MedicalRecordDTO;
import vn.wellcare4u.models.dto.doctor.PatientMedicalRecordsDTO;
import vn.wellcare4u.models.dto.doctor.PatientSummaryDTO;
import vn.wellcare4u.models.request.medical.CreateRecordRequest;


public interface MedicalRecordService {


	void finalizeRecord(CreateRecordRequest req, Long doctorId);

	Long startExam(Long appointmentId, Long doctorId);

	MedicalRecordDTO getRecordDetail(Long id);

	List<MedicalRecordDTO> getRecordsByPatient(Long patientId);

	List<MedicalRecordDTO> getRecordsByDoctor(Long doctorId);

	PatientMedicalRecordsDTO getPatientDetails(Long doctorId, Long patientId);

	List<PatientSummaryDTO> getPatientsSummaryByDoctor(Long doctorId);

	Long getRecordIdByAppointment(Long appointmentId);

}
