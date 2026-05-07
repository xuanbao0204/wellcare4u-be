package vn.wellcare4u.models.request.doctor;

import java.time.LocalTime;

import lombok.Data;

@Data
public class CreateScheduleRequest {
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
}