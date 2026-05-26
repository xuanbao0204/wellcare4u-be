package vn.wellcare4u.models.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.enums.ECancelBy;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AppointmentDTO {

    private Long id;

    private Long doctorId;
    private String doctorName;
    private String doctorAvatar;

    private Long patientId;
    private String patientName;
    private String patientAvatar;

    private Long slotId;
    private String slotTime;
    private String slotDate;

    private String reason;
    private EAppointmentType type;
    private EAppointmentStatus status;

    private LocalDateTime createdAt;
    
    private Long recordId;

    private ECancelBy cancelBy;

    private String cancelReason;

    private LocalDateTime cancelledAt;
    
    private Boolean checkedIn;
}