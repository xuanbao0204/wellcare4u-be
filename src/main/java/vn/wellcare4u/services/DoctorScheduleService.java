package vn.wellcare4u.services;

import java.util.List;

import vn.wellcare4u.models.dto.doctor.DoctorScheduleDTO;
import vn.wellcare4u.models.request.doctor.CreateScheduleRequest;
import vn.wellcare4u.models.request.doctor.UpdateScheduleRequest;

public interface DoctorScheduleService {

	DoctorScheduleDTO update(Long id, UpdateScheduleRequest req);

	DoctorScheduleDTO create(Long doctorId, CreateScheduleRequest req);

	List<DoctorScheduleDTO> getByDoctor(Long doctorId);

}
