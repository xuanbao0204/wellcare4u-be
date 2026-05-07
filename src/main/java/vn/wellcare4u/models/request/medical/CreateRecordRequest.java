package vn.wellcare4u.models.request.medical;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.dto.MedicalTestDTO;
import vn.wellcare4u.models.dto.PrescriptionItemDTO;
import vn.wellcare4u.models.dto.doctor.VitalSignDTO;
import vn.wellcare4u.models.request.AppointmentRequest;

@Data
@NoArgsConstructor
public class CreateRecordRequest {

	private Long recordId;
	private Long appointmentId;
	
	private Long doctorId;
	private Long patientId;
	
    private String chiefComplaint;
    private String symptoms;

    private String diagnosis;
    private String icdCode;

    private String treatmentPlan;
    private String conclusion;

    private AppointmentRequest followUpDate;

    private VitalSignDTO vital;

    private List<MedicalTestDTO> tests;

    private List<PrescriptionItemDTO> items;
}