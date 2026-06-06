package vn.wellcare4u.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.enums.EAppointmentEventType;

@Getter
@AllArgsConstructor
public class AppointmentEvent {

    private EAppointmentEventType type;

    private Appointment appointment;

    private String actor;

    private String reason;
}