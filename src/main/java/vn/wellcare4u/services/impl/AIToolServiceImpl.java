package vn.wellcare4u.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.models.ai.AiClient;
import vn.wellcare4u.models.request.SuggestSpecializationRequest;
import vn.wellcare4u.services.AIToolService;
import vn.wellcare4u.utils.AIPromptBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIToolServiceImpl implements AIToolService {
	
	private final AiClient ai;
	
	@Override
	public String summarizeMedicalHistory(List<MedicalRecord> records, Patient patient) {
		if (records.isEmpty() || records.size() == 0) return "Chưa có dữ liệu để thống kê";
		return ai.prompt(AIPromptBuilder.PatientMedicalSummaryPrompt(records, patient));
	}

	@Override
	public String summarizeDoctorDashboard(Doctor doctor, List<Appointment> allAppointments,
			List<MedicalRecord> records) {
		if (records.isEmpty() && allAppointments.isEmpty()) return "Chưa có dữ liệu để thống kê";
		
		return ai.prompt(AIPromptBuilder.DoctorDashboardSummaryPrompt(doctor, allAppointments, records));
	}

	@Override
	public String getSuggestionSpecialization(SuggestSpecializationRequest req) {
		log.info("User symptom: " + req.getUserSymptom());
		return ai.prompt(AIPromptBuilder.RecommendSpeciality(req.getUserSymptom()));
	}
	
	@Override
	public String checkViolation(ForumPost p) {
		return ai.prompt(AIPromptBuilder.CheckViolation(p));
	}
}