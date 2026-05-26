package vn.wellcare4u.services;

import java.util.List;

import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.medical.MedicalRecord;

public interface AIService {

	String suggestSpecialty(String symptoms);

	String summarizeMedicalHistory(List<MedicalRecord> records, Patient patient);

	String summarizeDoctorDashboard(Doctor doctor, List<Appointment> appointments, List<MedicalRecord> records);

}
