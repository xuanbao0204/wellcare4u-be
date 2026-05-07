package vn.wellcare4u.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.doctor.DoctorException;
import vn.wellcare4u.entities.doctor.DoctorSchedule;
import vn.wellcare4u.entities.doctor.TimeSlot;
import vn.wellcare4u.enums.ETimeSlotStatus;
import vn.wellcare4u.models.dto.doctor.DoctorExceptionDTO;
import vn.wellcare4u.models.request.CreateExceptionRequest;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.doctor.DoctorExceptionRepository;
import vn.wellcare4u.repositories.doctor.DoctorScheduleRepository;
import vn.wellcare4u.repositories.doctor.TimeSlotRepository;
import vn.wellcare4u.services.DoctorExceptionService;
import vn.wellcare4u.services.TimeSlotService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorExceptionServiceImpl implements DoctorExceptionService {

    private final DoctorExceptionRepository exceptionRepo;
    private final DoctorRepository doctorRepo;
    private final DoctorScheduleRepository scheduleRepo;
    private final TimeSlotService timeSlotService;

    @Override
    @Transactional
    public DoctorExceptionDTO createDayOff(Long doctorId, CreateExceptionRequest req) {

        // Idempotent check
        if (exceptionRepo.existsByDoctorIdAndDate(doctorId, req.getDate())) {
            throw new IllegalStateException("Day-off already exists for date: " + req.getDate());
        }

        Doctor doctor = doctorRepo.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found: " + doctorId));

        // 1. Lưu exception
        DoctorException exception = new DoctorException();
        exception.setDoctor(doctor);
        exception.setDate(req.getDate());
        exception.setReason(req.getReason());
        exceptionRepo.save(exception);

        // 2. Block tất cả AVAILABLE slots trong ngày đó
        //    BOOKED slots giữ nguyên → bác sĩ/admin cần xử lý thủ công
        timeSlotService.applyDayOff(doctorId, req.getDate());

        log.info("Day-off created for doctorId={}, date={}", doctorId, req.getDate());
        return mapToDTO(exception);
    }

    @Override
    @Transactional
    public void revokeDayOff(Long doctorId, LocalDate date) {

        DoctorException exception = exceptionRepo.findByDoctorIdAndDate(doctorId, date)
            .orElseThrow(() -> new RuntimeException("Day-off not found"));

        exceptionRepo.delete(exception);

        // 1. Restore BLOCKED → AVAILABLE (chỉ slot trong tương lai)
        timeSlotService.restoreBlockedSlots(doctorId, date);

        // 2. Sinh lại slot nếu còn thiếu (trường hợp slot chưa từng được tạo)
        DayOfWeek dow = date.getDayOfWeek();
        List<DoctorSchedule> schedules = scheduleRepo
            .findByDoctorIdAndDayOfWeekAndIsActiveTrue(doctorId, dow.getValue());

        for (DoctorSchedule schedule : schedules) {
            timeSlotService.generateSlotsForDate(schedule, date);
        }

        log.info("Day-off revoked for doctorId={}, date={}", doctorId, date);
    }
    
    @Override
    public List<DoctorExceptionDTO> getDayOffsByDoctor(Long doctorId) {
        return exceptionRepo.findByDoctorId(doctorId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    private DoctorExceptionDTO mapToDTO(DoctorException e) {
        return DoctorExceptionDTO.builder()
            .id(e.getId())
            .doctorId(e.getDoctor().getId())
            .doctorName(e.getDoctor().getFirstName() + " " + e.getDoctor().getLastName())
            .date(e.getDate())
            .reason(e.getReason())
            .build();
    }
}

//@Service
//@RequiredArgsConstructor
//public class DoctorExceptionServiceImpl implements DoctorExceptionService {
//
//    private final DoctorExceptionRepository exceptionRepo;
//    private final TimeSlotRepository timeSlotRepo;
//    private final DoctorRepository doctorRepo;
//
//    @Override
//    @Transactional
//    public void addDayOff(Long doctorId, LocalDate date, String reason) {
//
//        Doctor doctor = doctorRepo.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//
//        boolean exists = exceptionRepo.existsByDoctorIdAndDate(doctorId, date);
//        if (exists) {
//            throw new RuntimeException("Day off already exists");
//        }
//
//        DoctorException ex = new DoctorException();
//        ex.setDoctor(doctor);
//        ex.setDate(date);
//        ex.setReason(reason);
//
//        exceptionRepo.save(ex);
//
//        List<TimeSlot> slots = timeSlotRepo.findByDoctorIdAndDate(doctorId, date);
//
//        for (TimeSlot slot : slots) {
//
//            if (slot.getStatus() != ETimeSlotStatus.BOOKED) {
//                slot.setStatus(ETimeSlotStatus.BLOCKED);
//            }
//        }
//
//        timeSlotRepo.saveAll(slots);
//    }
//
//    @Override
//    public List<DoctorException> getByDoctor(Long doctorId) {
//        return exceptionRepo.findByDoctorId(doctorId);
//    }
//
//    @Override
//    @Transactional
//    public void removeDayOff(Long exceptionId) {
//
//        DoctorException ex = exceptionRepo.findById(exceptionId)
//                .orElseThrow(() -> new RuntimeException("Day off not found"));
//
//        Long doctorId = ex.getDoctor().getId();
//        LocalDate date = ex.getDate();
//
//        exceptionRepo.delete(ex);
//
//        List<TimeSlot> slots = timeSlotRepo.findByDoctorIdAndDate(doctorId, date);
//
//        for (TimeSlot slot : slots) {
//            if (slot.getStatus() == ETimeSlotStatus.BLOCKED) {
//                slot.setStatus(ETimeSlotStatus.AVAILABLE);
//            }
//        }
//
//        timeSlotRepo.saveAll(slots);
//    }
//}