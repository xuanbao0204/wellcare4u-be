package vn.wellcare4u.models.request.doctor;

import java.time.LocalTime;

import lombok.Data;

@Data
public class UpdateScheduleRequest {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Boolean isActive;
}
