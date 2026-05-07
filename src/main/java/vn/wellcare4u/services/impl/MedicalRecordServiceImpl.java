package vn.wellcare4u.services.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.entities.medical.MedicalTest;
import vn.wellcare4u.entities.medical.Prescription;
import vn.wellcare4u.entities.medical.PrescriptionItem;
import vn.wellcare4u.entities.medical.VitalSign;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.ENotificationType;
import vn.wellcare4u.enums.ERecordStatus;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.dto.MedicalRecordDTO;
import vn.wellcare4u.models.dto.MedicalTestDTO;
import vn.wellcare4u.models.dto.PatientDTO;
import vn.wellcare4u.models.dto.PrescriptionItemDTO;
import vn.wellcare4u.models.dto.doctor.DoctorDTO;
import vn.wellcare4u.models.dto.doctor.PatientMedicalRecordsDTO;
import vn.wellcare4u.models.dto.doctor.PatientSummaryDTO;
import vn.wellcare4u.models.dto.doctor.VitalSignDTO;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.models.request.medical.CreateRecordRequest;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.medical.MedicalRecordRepository;
import vn.wellcare4u.repositories.medical.MedicalTestRepository;
import vn.wellcare4u.repositories.medical.PrescriptionItemRepository;
import vn.wellcare4u.repositories.medical.PrescriptionRepository;
import vn.wellcare4u.repositories.medical.VitalSignRepository;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.MedicalRecordService;
import vn.wellcare4u.services.NotificationService;

@Service
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

	@Autowired
	private MedicalRecordRepository recordRepo;
	@Autowired
	private AppointmentRepository appointmentRepo;
	@Autowired
	private MedicalTestRepository testRepo;
	@Autowired
	private PrescriptionRepository prescriptionRepo;
	@Autowired
	private PrescriptionItemRepository itemRepo;
	
	@Autowired
	private NotificationService notiServ;

	@Autowired
	private AppointmentService apptServ;
	
	@Autowired
	private VitalSignRepository vitalSignRepo;

	@Override
	public Long startExam(Long appointmentId, Long doctorId) {

		Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow();

		boolean isInProgress = appointment.getStatus() == EAppointmentStatus.IN_PROGRESS;

		boolean isEligibleToStart = (appointment.getStatus() == EAppointmentStatus.CONFIRMED
				&& Boolean.TRUE.equals(appointment.getCheckedIn()))
				|| appointment.getStatus() == EAppointmentStatus.CHECKED_IN;

		if (!isInProgress && !isEligibleToStart) {
			throw new RuntimeException("Appointment not ready for examination");
		}

		Optional<MedicalRecord> existing = recordRepo.findByAppointmentId(appointmentId);
		if (existing.isPresent()) {
			return existing.get().getId();
		}

		if (isInProgress) {
			throw new RuntimeException("Inconsistent state: IN_PROGRESS but no medical record");
		}

		MedicalRecord record = new MedicalRecord();
		record.setAppointment(appointment);
		record.setPatient(appointment.getPatient());
		record.setDoctor(appointment.getDoctor());
		record.setStatus(ERecordStatus.DRAFT);
		record.setCreatedAt(LocalDateTime.now());
		record.setChiefComplaint(appointment.getReason());

		recordRepo.save(record);

		appointment.setStatus(EAppointmentStatus.IN_PROGRESS);
		appointmentRepo.save(appointment);

		return record.getId();
	}

	@Override
	public Long getRecordIdByAppointment(Long appointmentId) {
		Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow();

		if (appointment.getStatus() != EAppointmentStatus.CONFIRMED) {
			throw new RuntimeException("Appointment not approved");
		}

		Optional<MedicalRecord> existing = recordRepo.findByAppointmentId(appointmentId);

		if (existing.isPresent()) {
			return existing.get().getId();
		}
		return null;
	}

	@Override
	public MedicalRecordDTO getRecordDetail(Long id) {

		MedicalRecord record = recordRepo.findDetailById(id).orElseThrow();

		VitalSign vital = vitalSignRepo.findTopByMedicalRecordIdOrderByTimestampDesc(id).orElse(null);

		List<MedicalTest> tests = testRepo.findByMedicalRecordId(id);

		List<PrescriptionItem> items = itemRepo.findByRecordId(id);

		return mapToDTO(record, vital, tests, items);
	}

	@Override
	public List<MedicalRecordDTO> getRecordsByPatient(Long patientId) {
		return recordRepo.findByPatientId(patientId).stream().map(this::mapToListDTO).toList();
	}

	@Override
	public List<MedicalRecordDTO> getRecordsByDoctor(Long doctorId) {
		return recordRepo.findByDoctorId(doctorId).stream().map(this::mapToListDTO).toList();
	}
	
	@Override
	public List<PatientSummaryDTO> getPatientsSummaryByDoctor(Long doctorId) {

	    List<MedicalRecord> records = recordRepo.findAllByDoctorId(doctorId);

	    if (records.isEmpty()) return Collections.emptyList();

	    Map<Long, List<MedicalRecord>> grouped = records.stream()
	        .collect(Collectors.groupingBy(
	            r -> r.getPatient().getId(),
	            LinkedHashMap::new,
	            Collectors.toList()
	        ));

	    return grouped.values().stream()
	        .map(this::mapToPatientSummary)
	        .sorted(Comparator.comparing(
	            PatientSummaryDTO::getLastVisitDate,
	            Comparator.reverseOrder()
	        ))
	        .toList();
	}
	
	private PatientSummaryDTO mapToPatientSummary(List<MedicalRecord> records) {

	    MedicalRecord latest = records.get(0);
	    Patient p = latest.getPatient();

	    return PatientSummaryDTO.builder()
	        .patientId(p.getId())
	        .firstName(p.getFirstName())
	        .lastName(p.getLastName())
	        .avatar(p.getAvatar())
	        .gender(p.getGender())
	        .dob(p.getDob())
	        .totalRecords(records.size())
	        .lastVisitDate(latest.getCreatedAt().toLocalDate())
	        .build();
	}
	
	@Override
	public PatientMedicalRecordsDTO getPatientDetails(Long doctorId, Long patientId) {
		
	    List<MedicalRecord> records =
	        recordRepo.findAllByDoctorIdAndPatientId(doctorId, patientId);

	    if (records.isEmpty()) {
	        throw new RuntimeException("No records found");
	    }

	    Patient patient = records.get(0).getPatient();

	    List<VitalSignDTO> vitalSigns = vitalSignRepo
	        .findByPatientIdInOrderByTimestampDesc(patientId)
	        .stream()
	        .map(this::mapToVitalSignDTO)
	        .toList();

	    return PatientMedicalRecordsDTO.builder()
	        .patientId(patientId)
	        .firstName(patient.getFirstName())
	        .lastName(patient.getLastName())
	        .avatar(patient.getAvatar())
	        .gender(patient.getGender())
	        .dob(patient.getDob())
	        .totalRecords(records.size())
	        .lastVisitDate(records.get(0).getCreatedAt().toLocalDate())
	        .records(mapToRecordSummaries(records))
	        .vitalSigns(vitalSigns)
	        .build();
	}
	
    private VitalSignDTO mapToVitalSignDTO(VitalSign v) {
        return VitalSignDTO.builder()
            .height(v.getHeight())
            .weight(v.getWeight())
            .bmi(v.getBmi())
            .bloodPressure(v.getBloodPressure())
            .heartRate(v.getHeartRate())
            .bloodSugar(v.getBloodSugar())
            .build();
    }
    
    private List<PatientMedicalRecordsDTO.RecordSummary> mapToRecordSummaries(
    	    List<MedicalRecord> records
    	) {
    	    return records.stream()
    	        .map(r -> PatientMedicalRecordsDTO.RecordSummary.builder()
    	            .recordId(r.getId())
    	            .diagnosis(r.getDiagnosis())
    	            .createdAt(r.getCreatedAt())
    	            .build())
    	        .toList();
    	}

	@Override
	@Transactional
	public void finalizeRecord(CreateRecordRequest req, Long doctorId) {

		MedicalRecord record = recordRepo.findById(req.getRecordId()).orElseThrow();

		if (doctorId != record.getDoctor().getId()) {
			throw new AppException("Truy vấn không hợp lệ", "INVALID_QUERY", HttpStatus.BAD_REQUEST);
		}

		if (record.getStatus() == ERecordStatus.FINALIZED) {
			throw new RuntimeException("Already finalized");
		}

		Appointment appointment = record.getAppointment();

		if (appointment.getStatus() != EAppointmentStatus.IN_PROGRESS ) {
			throw new RuntimeException("Invalid appointment state");
		}

		record.setChiefComplaint(req.getChiefComplaint());
		record.setSymptoms(req.getSymptoms());
		record.setDiagnosis(req.getDiagnosis());
		record.setIcdCode(req.getIcdCode());
		record.setTreatmentPlan(req.getTreatmentPlan());
		record.setConclusion(req.getConclusion());
		record.setStatus(ERecordStatus.FINALIZED);

		// Vital
		if (req.getVital() != null) {
			VitalSign v = new VitalSign();
			v.setMedicalRecord(record);
			v.setHeight(req.getVital().getHeight());
			v.setWeight(req.getVital().getWeight());

			if (v.getHeight() != null && v.getWeight() != null) {
				double bmi = v.getWeight() / Math.pow(v.getHeight() / 100, 2);
				v.setBmi(bmi);
			}

			v.setBloodPressure(req.getVital().getBloodPressure());
			v.setHeartRate(req.getVital().getHeartRate());
			v.setBloodSugar(req.getVital().getBloodSugar());
			v.setTimestamp(LocalDateTime.now());
			v.setPatient(record.getPatient());

			vitalSignRepo.save(v);
		}

		// Tests
		if (req.getTests() != null) {
			for (MedicalTestDTO t : req.getTests()) {
				MedicalTest mt = new MedicalTest();
				mt.setMedicalRecord(record);
				mt.setTestName(t.getTestName());
				mt.setResultText(t.getResultText());
				mt.setConclusion(t.getConclusion());
				mt.setImageUrl(t.getImageUrl());
				mt.setPerformedAt(LocalDateTime.now());

				testRepo.save(mt);
			}
		}

		// Prescription
		if (req.getItems() != null && !req.getItems().isEmpty()) {
			Prescription prescription = new Prescription();
			prescription.setMedicalRecord(record);
			prescription.setCreatedAt(LocalDateTime.now());

			prescriptionRepo.save(prescription);

			for (PrescriptionItemDTO i : req.getItems()) {
				PrescriptionItem item = new PrescriptionItem();
				item.setPrescription(prescription);
				item.setDrug(i.getDrug());
				item.setDosage(i.getDosage());
				item.setFrequency(i.getFrequency());
				item.setDuration(i.getDuration());
				item.setInstruction(i.getInstruction());

				itemRepo.save(item);
			}
		}

		if (req.getFollowUpDate() != null) {
			AppointmentDTO apptDTO = apptServ.bookSlot(req.getFollowUpDate(), record.getPatient().getId());
			Appointment a = appointmentRepo.findById(apptDTO.getId()).orElseThrow(
					() -> new AppException("Không tìm thấy lịch hẹn", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
			record.setFollowUpDate(a);

		}

		appointment.setStatus(EAppointmentStatus.COMPLETED);
		recordRepo.save(record);
		appointmentRepo.save(appointment);
		
		notiServ.send(
		        NotificationRequest.toUsers(
		        		List.of(
		            appointment.getPatient().getId(),
		            appointment.getDoctor().getId()),
		        		
		            ENotificationType.INFO,
		            "Cập nhật về lịch hẹn",
		            "Lịch hẹn với mã #" + appointment.getId() + " đã hoàn thành",
		            appointment.getId()		        
		            )
		    );
		
	}

	private MedicalRecordDTO mapToListDTO(MedicalRecord record) {
		return MedicalRecordDTO.builder().recordId(record.getId()).doctor(mapDoctor(record.getDoctor()))
				.patient(mapPatient(record.getPatient())).chiefComplaint(record.getChiefComplaint())
				.diagnosis(record.getDiagnosis()).createdAt(record.getCreatedAt()).build();
	}

	private MedicalRecordDTO mapToDTO(MedicalRecord record, VitalSign vital, List<MedicalTest> tests,
			List<PrescriptionItem> items) {

		return MedicalRecordDTO.builder().recordId(record.getId()).doctor(mapDoctor(record.getDoctor()))
				.patient(mapPatient(record.getPatient())).chiefComplaint(record.getChiefComplaint())
				.symptoms(record.getSymptoms()).diagnosis(record.getDiagnosis()).icdCode(record.getIcdCode())
				.treatmentPlan(record.getTreatmentPlan()).conclusion(record.getConclusion())
				.followUpDate(
						record.getFollowUpDate() != null ? record.getFollowUpDate().getTimeSlot().getDate() : null)
				.vitalSign(mapVital(vital)).tests(mapTests(tests)).items(mapItems(items))
				.createdAt(record.getCreatedAt()).build();
	}

	private DoctorDTO mapDoctor(Doctor d) {
		if (d == null)
			return null;

		return DoctorDTO.builder().firstName(d.getFirstName()).lastName(d.getLastName())
				.specialization(d.getSpecialization().getDisplayName()).email(d.getAccount().getEmail())
				.avatar(d.getAvatar()).build();
	}

	private VitalSignDTO mapVital(VitalSign v) {
		if (v == null)
			return null;

		return VitalSignDTO.builder().height(v.getHeight()).weight(v.getWeight()).bmi(v.getBmi())
				.bloodPressure(v.getBloodPressure()).heartRate(v.getHeartRate()).bloodSugar(v.getBloodSugar()).build();
	}

	private List<MedicalTestDTO> mapTests(List<MedicalTest> tests) {
		return tests.stream().map(t -> MedicalTestDTO.builder().testName(t.getTestName()).resultText(t.getResultText())
				.conclusion(t.getConclusion()).imageUrl(t.getImageUrl()).build()).toList();
	}

	private List<PrescriptionItemDTO> mapItems(List<PrescriptionItem> items) {
		return items.stream()
				.map(i -> PrescriptionItemDTO.builder().drug(i.getDrug()).dosage(i.getDosage())
						.frequency(i.getFrequency()).duration(i.getDuration()).instruction(i.getInstruction()).build())
				.toList();
	}

	private PatientDTO mapPatient(Patient p) {
		if (p == null)
			return null;

		return PatientDTO.builder().email(p.getAccount().getEmail()).firstName(p.getFirstName())
				.lastName(p.getLastName()).gender(p.getGender()).avatar(p.getAvatar())
				.emergencyContact(p.getEmergencyContact()).bloodType(p.getBloodType())
				.insuranceNumber(p.getInsuranceNumber()).build();
	}
}
