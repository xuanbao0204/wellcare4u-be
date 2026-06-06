package vn.wellcare4u.services;

import java.util.List;

import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.models.request.SuggestSpecializationRequest;

public interface AIToolService {

	String summarizeMedicalHistory(List<MedicalRecord> records, Patient patient);

	String summarizeDoctorDashboard(Doctor doctor, List<Appointment> allAppointments, List<MedicalRecord> records);

	String getSuggestionSpecialization(SuggestSpecializationRequest req);

	String checkViolation(ForumPost p);

}
