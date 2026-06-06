package vn.wellcare4u.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.enums.ENotificationType;
import vn.wellcare4u.events.AppointmentEvent;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.services.NotificationService;

@Component
@RequiredArgsConstructor
public class AppointmentNotificationListener {

	private final NotificationService notiServ;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(AppointmentEvent event) {

		try {

			switch (event.getType()) {

			case BOOKED -> handleBooked(event);

			case CONFIRMED -> handleConfirmed(event);

			case CANCELLED -> handleCancelled(event);

			case COMPLETED -> handleCompleted(event);
			
			case REBOOK -> handleRebook(event);
			
			case FOLLOW_UP -> handleFollowUp(event);
			
			case PATIENT_CHECK_IN -> handlePatientCheckIn(event);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void handlePatientCheckIn(AppointmentEvent event) {
		Appointment appt = event.getAppointment();

		notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.INFO, "Lịch hẹn",
				"Bạn đã checkin thành công. Mã lịch hẹn: #" + appt.getId(), null));

		notiServ.send(NotificationRequest.toUser(appt.getDoctor().getId(), ENotificationType.INFO, "Lịch hẹn mới",
				"Bệnh nhân " + appt.getPatient().getFullName() + " đã đến. Mã lịch hẹn: #" + appt.getId(), null));
	}

	private void handleBooked(AppointmentEvent event) {

		Appointment appt = event.getAppointment();

		notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.INFO, "Lịch hẹn",
				"Lịch hẹn đã được tạo thành công. Mã lịch hẹn: #" + appt.getId(), null));

		notiServ.send(NotificationRequest.toUser(appt.getDoctor().getId(), ENotificationType.INFO, "Lịch hẹn mới",
				"Có lịch hẹn mới đang chờ xác nhận. Mã lịch hẹn: #" + appt.getId(), null));
	}
	
	private void handleRebook(AppointmentEvent event) {

		Appointment appt = event.getAppointment();

		notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.INFO, "Lịch hẹn",
				"Lịch hẹn đã được tạo lại thành công do bị hủy. Mã lịch hẹn: #" + appt.getId(), null));

		notiServ.send(NotificationRequest.toUser(appt.getDoctor().getId(), ENotificationType.INFO, "Lịch hẹn mới",
				"Bạn đã tạo lịch hẹn lại thành công. Mã lịch hẹn: #" + appt.getId(), null));
	}
	
	private void handleFollowUp(AppointmentEvent event) {

		Appointment appt = event.getAppointment();

		notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.INFO, "Lịch hẹn mới",
				"Lịch hẹn tái khán đã được cập nhật, xem chi tiết trong phần Lịch hẹn. Mã lịch hẹn: #" + appt.getId(), null));

		notiServ.send(NotificationRequest.toUser(appt.getDoctor().getId(), ENotificationType.INFO, "Lịch hẹn mới",
				"Bạn đã tạo lịch hẹn tái khám thành công cho bệnh nhân: " + appt.getPatient().getId() + " - " + appt.getPatient().getFullName() + ". Mã lịch hẹn: #" + appt.getId(), null));
	}

	private void handleConfirmed(AppointmentEvent event) {

		Appointment appt = event.getAppointment();

		notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.INFO,
				"Lịch hẹn đã xác nhận", "Bác sĩ " + appt.getDoctor().getFullName() + " đã xác nhận lịch hẹn của bạn. Mã lịch hẹn: #" + appt.getId(), null));
	}

	private void handleCancelled(AppointmentEvent event) {

		Appointment appt = event.getAppointment();

		String actor = event.getActor();

		String msg = "Lịch hẹn đã bị hủy";

		if ("PATIENT".equals(actor)) {

			msg = "Bệnh nhân đã hủy lịch hẹn";

			notiServ.send(NotificationRequest.toUser(appt.getDoctor().getId(), ENotificationType.WARNING,
					"Lịch hẹn bị hủy. Mã lịch hẹn: #" + appt.getId(), msg, null));

		} else {

			msg = "Lịch hẹn đã bị hủy bởi bác sĩ";

			notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.WARNING,
					"Lịch hẹn bị hủy. Mã lịch hẹn: #" + appt.getId(), msg, null));
		}
	}

	private void handleCompleted(AppointmentEvent event) {

		Appointment appt = event.getAppointment();

		notiServ.send(NotificationRequest.toUser(appt.getPatient().getId(), ENotificationType.INFO, "Hoàn tất khám",
				"Buổi khám đã hoàn tất. Mã lịch hẹn: #" + appt.getId(), null));
	}
}