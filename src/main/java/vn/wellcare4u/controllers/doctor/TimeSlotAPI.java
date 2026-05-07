package vn.wellcare4u.controllers.doctor;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.services.TimeSlotService;

@RestController
@RequestMapping("/api/v1/time-slots")
@RequiredArgsConstructor
public class TimeSlotAPI {

    private final TimeSlotService timeSlotService;

    @GetMapping("/doctor/{doctorId}")
    public ApiResponse<?> getSlotsByDoctor(
            @PathVariable Long doctorId,
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {

        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        return ApiResponse.builder()
                .status(200)
                .message("Slots retrieved successfully")
                .data(timeSlotService.getSlotsByDoctor(doctorId, from, to))
                .build();
    }

    @PutMapping("/{id}/block")
    public ApiResponse<?> blockSlot(@PathVariable Long id) {

        timeSlotService.blockSlot(id);

        return ApiResponse.builder()
                .status(200)
                .message("Slot blocked successfully")
                .build();
    }
    
    @PutMapping("/{id}/unblock")
    public ApiResponse<?> unblockSlot(@PathVariable Long id) {

        timeSlotService.unblockSlot(id);

        return ApiResponse.builder()
                .status(200)
                .message("Slot unblocked successfully")
                .build();
    }
    
    @GetMapping("/doctor/{doctorId}/available")
    public ApiResponse<?> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam String date
    ) {

        return ApiResponse.builder()
                .status(200)
                .message("Available slots retrieved")
                .data(timeSlotService.getAvailableSlots(doctorId, LocalDate.parse(date)))
                .build();
    }
}