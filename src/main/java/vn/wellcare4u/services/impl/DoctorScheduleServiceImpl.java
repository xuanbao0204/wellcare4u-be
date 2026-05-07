package vn.wellcare4u.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.wellcare4u.entities.doctor.DoctorSchedule;
import vn.wellcare4u.models.dto.doctor.DoctorScheduleDTO;
import vn.wellcare4u.models.request.doctor.CreateScheduleRequest;
import vn.wellcare4u.models.request.doctor.UpdateScheduleRequest;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.doctor.DoctorScheduleRepository;
import vn.wellcare4u.services.DoctorScheduleService;
import vn.wellcare4u.services.TimeSlotService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleServiceImpl implements DoctorScheduleService{

	@Autowired
	private DoctorScheduleRepository scheduleRepo;
	
	@Autowired
	private DoctorRepository doctorRepo;
	
	private final TimeSlotService timeSlotService;
	
	@Override
	public List<DoctorScheduleDTO> getByDoctor(Long doctorId) {

	    return scheduleRepo.findByDoctorId(doctorId)
	        .stream()
	        .map(this::mapToDTO)
	        .toList();
	}
	
	@Override
	@Transactional
    public DoctorScheduleDTO create(Long doctorId, CreateScheduleRequest req) {

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctorRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found")));
        schedule.setDayOfWeek(req.getDayOfWeek());
        schedule.setStartTime(req.getStartTime());
        schedule.setEndTime(req.getEndTime());
        schedule.setSlotDurationMinutes(req.getSlotDurationMinutes());
        schedule.setIsActive(true);

        scheduleRepo.save(schedule);

        timeSlotService.generateSlots(schedule, 30);

        return mapToDTO(schedule);
    }
	
	@Override
	@Transactional
	public DoctorScheduleDTO update(Long id, UpdateScheduleRequest req) {

	    DoctorSchedule schedule = scheduleRepo.findById(id)
	        .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));

	    // 1. Hủy AVAILABLE slots không còn hợp lệ với schedule mới
	    //    BOOKED slots sẽ được giữ nguyên
	    timeSlotService.cancelOutdatedAvailableSlots(
	        schedule,
	        req.getStartTime(),
	        req.getEndTime(),
	        req.getSlotDurationMinutes()
	    );

	    // 2. Cập nhật schedule
	    schedule.setStartTime(req.getStartTime());
	    schedule.setEndTime(req.getEndTime());
	    schedule.setSlotDurationMinutes(req.getSlotDurationMinutes());
	    schedule.setIsActive(req.getIsActive());
	    scheduleRepo.save(schedule);

	    // 3. Sinh slot mới (idempotent — đã có exists check, BOOKED không bị ảnh hưởng)
	    if (Boolean.TRUE.equals(req.getIsActive())) {
	        timeSlotService.refreshSlots(schedule);
	    }

	    return mapToDTO(schedule);
	}
	
//	@Override
//	@Transactional
//	public DoctorScheduleDTO update(Long id, UpdateScheduleRequest req) {
//
//	    DoctorSchedule schedule = scheduleRepo.findById(id)
//	        .orElseThrow(() -> new RuntimeException("Not found"));
//
//	    schedule.setStartTime(req.getStartTime());
//	    schedule.setEndTime(req.getEndTime());
//	    schedule.setSlotDurationMinutes(req.getSlotDurationMinutes());
//	    schedule.setIsActive(req.getIsActive());
//
//	    scheduleRepo.save(schedule);
//
//	    timeSlotService.refreshSlots(schedule);
//
//	    return mapToDTO(schedule);
//	}
	
	public DoctorScheduleDTO mapToDTO(DoctorSchedule schedule) {

        return DoctorScheduleDTO.builder()
	        .id(schedule.getId())
	        .dayOfWeek(schedule.getDayOfWeek())
	        .dayOfWeekName(getDayName(schedule.getDayOfWeek()))
	        .startTime(schedule.getStartTime())
	        .endTime(schedule.getEndTime())
	        .slotDurationMinutes(schedule.getSlotDurationMinutes())
	        .isActive(schedule.getIsActive())
	        .build();

    }

    private String getDayName(Integer day) {
        switch (day) {
            case 1: return "Thứ 2";
            case 2: return "Thứ 3";
            case 3: return "Thứ 4";
            case 4: return "Thứ 5";
            case 5: return "Thứ 6";
            case 6: return "Thứ 7";
            case 7: return "Chủ nhật";
            default: return "";
        }
    }
    
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void rollForwardSlots() {
        log.info("[Scheduler] Starting daily slot generation...");

        List<DoctorSchedule> activeSchedules = scheduleRepo.findByIsActiveTrue();

        for (DoctorSchedule schedule : activeSchedules) {
            try {
                timeSlotService.generateSlots(schedule, 30);
            } catch (Exception e) {
                log.error("[Scheduler] Failed to generate slots for scheduleId={}", schedule.getId(), e);
                // Không throw để các schedule khác vẫn tiếp tục chạy
            }
        }

        log.info("[Scheduler] Done. Processed {} schedules.", activeSchedules.size());
    }
}
