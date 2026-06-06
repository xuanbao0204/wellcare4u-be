package vn.wellcare4u.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.models.dto.ProfileCompletionResult;
import vn.wellcare4u.services.ProfileCompletionService;

@Service
public class ProfileCompletionServiceImpl implements ProfileCompletionService {

	@Override
	public ProfileCompletionResult calculate(User user) {

		List<String> missing = new ArrayList<>();

		int totalRequired = 0;

		// ===== COMMON =====

		totalRequired++;
		if (isBlank(user.getFirstName()))
			missing.add("firstName");

		totalRequired++;
		if (isBlank(user.getLastName()))
			missing.add("lastName");

		totalRequired++;
		if (user.getDob() == null)
			missing.add("dob");

		totalRequired++;
		if (isBlank(user.getGender()))
			missing.add("gender");

		totalRequired++;
		if (isBlank(user.getPhone()))
			missing.add("phone");

		// ===== PATIENT =====

		if (user instanceof Patient patient) {

			totalRequired++;
			if (isBlank(patient.getEmergencyContact()))
				missing.add("emergencyContact");

		}

		// ===== DOCTOR =====

		if (user instanceof Doctor doctor) {

			totalRequired++;
			if (doctor.getSpecialization() == null)
				missing.add("specialization");

			totalRequired++;
			if (isBlank(doctor.getCertification()))
				missing.add("certification");

			totalRequired++;
			if (doctor.getExperienceYears() == null)
				missing.add("experienceYears");

			totalRequired++;
			if (doctor.getConsultationFee() == null)
				missing.add("consultationFee");

			totalRequired++;
			if (isBlank(doctor.getClinicAddress()))
				missing.add("clinicAddress");
		}

		int completedFields = totalRequired - missing.size();

		int percentage = (int) Math.round(completedFields * 100.0 / totalRequired);

		return new ProfileCompletionResult(missing.isEmpty(), percentage, missing);
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}