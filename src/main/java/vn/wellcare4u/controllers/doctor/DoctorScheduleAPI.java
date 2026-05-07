package vn.wellcare4u.controllers.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.doctor.CreateScheduleRequest;
import vn.wellcare4u.models.request.doctor.UpdateScheduleRequest;
import vn.wellcare4u.services.DoctorScheduleService;

@RestController
@RequestMapping("/api/v1/doctor/schedules")
public class DoctorScheduleAPI {

	@Autowired
	private DoctorScheduleService scheduleService;

	@GetMapping("/get/{doctorId}")
	public ApiResponse<?> getSchedulesByDoctor(@PathVariable Long doctorId) {

		return ApiResponse.builder().status(200).message("Schedules retrieved successfully")
				.data(scheduleService.getByDoctor(doctorId)).build();
	}

	@PostMapping("/create/{doctorId}")
	public ApiResponse<?> createSchedule(@PathVariable Long doctorId, @RequestBody CreateScheduleRequest request) {

		return ApiResponse.builder().status(201).message("Schedule created successfully")
				.data(scheduleService.create(doctorId, request)).build();
	}

	@PutMapping("/{id}")
	public ApiResponse<?> updateSchedule(@PathVariable Long id, @RequestBody UpdateScheduleRequest request) {

		return ApiResponse.builder().status(200).message("Schedule updated successfully")
				.data(scheduleService.update(id, request)).build();
	}
}
