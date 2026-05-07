package vn.wellcare4u.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.wellcare4u.entities.doctor.DoctorSchedule;
import vn.wellcare4u.entities.doctor.TimeSlot;
import vn.wellcare4u.enums.ETimeSlotStatus;
import vn.wellcare4u.models.dto.doctor.TimeSlotDTO;
import vn.wellcare4u.repositories.doctor.DoctorExceptionRepository;
import vn.wellcare4u.repositories.doctor.TimeSlotRepository;
import vn.wellcare4u.services.TimeSlotService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepo;
    private final DoctorExceptionRepository exceptionRepo;

    @Override
    @Transactional
    public void generateSlots(DoctorSchedule schedule, int daysAhead) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {

            if (!date.getDayOfWeek().equals(DayOfWeek.of(schedule.getDayOfWeek()))) {
                continue;
            }

            if (exceptionRepo.existsByDoctorIdAndDate(
                    schedule.getDoctor().getId(), date)) {
                continue;
            }

            generateSlotsForDate(schedule, date); 
        }
    }

    @Override
    public void generateSlotsForDate(DoctorSchedule schedule, LocalDate date) {

        LocalTime start = schedule.getStartTime();

        while (true) {

            LocalTime end = start.plusMinutes(schedule.getSlotDurationMinutes());

            if (end.isAfter(schedule.getEndTime())) break;

            boolean exists = timeSlotRepo.existsByDoctorIdAndDateAndStartTime(
                    schedule.getDoctor().getId(),
                    date,
                    start
            );

            if (!exists) {
                TimeSlot slot = new TimeSlot();
                slot.setDoctor(schedule.getDoctor());
                slot.setSchedule(schedule);
                slot.setDate(date);
                slot.setStartTime(start);
                slot.setEndTime(end);
                slot.setStatus(ETimeSlotStatus.AVAILABLE);

                timeSlotRepo.save(slot);
            }

            start = end;
        }
    }

    @Override
    @Transactional
    public void refreshSlots(DoctorSchedule schedule) {
        generateSlots(schedule, 30);
    }
    
    @Override
	@Transactional
    public void cancelOutdatedAvailableSlots(DoctorSchedule schedule,
                                              LocalTime newStart,
                                              LocalTime newEnd,
                                              int newDurationMinutes) {
        LocalDate today = LocalDate.now();

        List<TimeSlot> futureAvailableSlots = timeSlotRepo
            .findByScheduleIdAndDateGreaterThanEqualAndStatus(
                schedule.getId(), today, ETimeSlotStatus.AVAILABLE
            );

        List<TimeSlot> toCancel = futureAvailableSlots.stream()
            .filter(slot -> {
                boolean outsideTimeRange = slot.getStartTime().isBefore(newStart)
                    || slot.getEndTime().isAfter(newEnd);

                long currentDuration = ChronoUnit.MINUTES.between(
                    slot.getStartTime(), slot.getEndTime()
                );
                boolean wrongDuration = currentDuration != newDurationMinutes;

                return outsideTimeRange || wrongDuration;
            })
            .peek(slot -> slot.setStatus(ETimeSlotStatus.BLOCKED))
            .collect(Collectors.toList());

        timeSlotRepo.saveAll(toCancel);
    }
    
    @Override
    @Transactional
    public void blockSlot(Long slotId) {

        TimeSlot slot = timeSlotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() == ETimeSlotStatus.BOOKED) {
            throw new RuntimeException("Cannot block booked slot");
        }

        slot.setStatus(ETimeSlotStatus.BLOCKED);
    }
    
    @Override
    @Transactional
    public void unblockSlot(Long id) {

        TimeSlot slot = timeSlotRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() == ETimeSlotStatus.BLOCKED) {
            slot.setStatus(ETimeSlotStatus.AVAILABLE);
        }

        timeSlotRepo.save(slot);
    }

    @Override
    public List<TimeSlotDTO> getSlotsByDoctor(Long doctorId, LocalDate from, LocalDate to) {

        return timeSlotRepo.findByDoctorIdAndDateBetween(doctorId, from, to)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private TimeSlotDTO mapToDTO(TimeSlot slot) {

        return TimeSlotDTO.builder()
                .id(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus().name())
                .build();
    }
    
    @Override
	@Transactional
    public void applyDayOff(Long doctorId, LocalDate date) {
        List<TimeSlot> slots = timeSlotRepo.findByDoctorIdAndDate(doctorId, date);

        List<TimeSlot> toBlock = slots.stream()
            .filter(slot -> slot.getStatus() == ETimeSlotStatus.AVAILABLE)
            .peek(slot -> slot.setStatus(ETimeSlotStatus.BLOCKED))
            .collect(Collectors.toList());

        timeSlotRepo.saveAll(toBlock);
        log.info("Blocked {} slots for doctorId={}, date={}", toBlock.size(), doctorId, date);

        // Log cảnh báo nếu có BOOKED slot — cần xử lý thủ công
        long bookedCount = slots.stream()
            .filter(s -> s.getStatus() == ETimeSlotStatus.BOOKED)
            .count();
        if (bookedCount > 0) {
            log.warn("doctorId={} has {} BOOKED slots on {} — manual handling required!",
                doctorId, bookedCount, date);
        }
    }

    // Thêm method restore
    @Override
	@Transactional
    public void restoreBlockedSlots(Long doctorId, LocalDate date) {
        List<TimeSlot> slots = timeSlotRepo.findByDoctorIdAndDate(doctorId, date);

        List<TimeSlot> toRestore = slots.stream()
            .filter(slot -> slot.getStatus() == ETimeSlotStatus.BLOCKED)
            .peek(slot -> slot.setStatus(ETimeSlotStatus.AVAILABLE))
            .collect(Collectors.toList());

        timeSlotRepo.saveAll(toRestore);
    }
    
    @Override
	public List<TimeSlotDTO> getAvailableSlots(Long doctorId, LocalDate date) {

        return timeSlotRepo.findAvailableSlotsValid(doctorId, date)
                .stream()
                .filter(s -> s.getStatus() == ETimeSlotStatus.AVAILABLE)
                .map(this::mapToDTO)
                .toList();
    }
    
    @Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void deletePastSlot() {
		timeSlotRepo.deleteAvailableSlots();
	}
}