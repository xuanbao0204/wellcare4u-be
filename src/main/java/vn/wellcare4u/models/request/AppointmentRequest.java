package vn.wellcare4u.models.request;

import lombok.Data;
import vn.wellcare4u.enums.EAppointmentType;

@Data
public class AppointmentRequest {
    private Long slotId;
    private Long doctorId;
    private Long patientId;
    private String reason;
    private EAppointmentType type;
}