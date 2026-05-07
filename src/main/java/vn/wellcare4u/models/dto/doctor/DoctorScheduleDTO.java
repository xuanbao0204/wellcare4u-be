package vn.wellcare4u.models.dto.doctor;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorScheduleDTO {

    private Long id;

    private Integer dayOfWeek;
    private String dayOfWeekName;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer slotDurationMinutes;

    private Boolean isActive;
}