package vn.wellcare4u.services;

import java.time.LocalDate;
import java.util.List;

import vn.wellcare4u.entities.doctor.DoctorException;
import vn.wellcare4u.models.dto.doctor.DoctorExceptionDTO;
import vn.wellcare4u.models.request.CreateExceptionRequest;

public interface DoctorExceptionService {

	List<DoctorExceptionDTO> getDayOffsByDoctor(Long doctorId);

	void revokeDayOff(Long doctorId, LocalDate date);

	DoctorExceptionDTO createDayOff(Long doctorId, CreateExceptionRequest req);

//	void addDayOff(Long doctorId, LocalDate date, String reason);
//
//	List<DoctorException> getByDoctor(Long doctorId);
//
//	void removeDayOff(Long exceptionId);

}
