package vn.wellcare4u.models.request;

import lombok.Data;
import vn.wellcare4u.enums.ECancelBy;

@Data
public class CancelAppointmentRequest {
	String reason;
	ECancelBy cancelBy;
}
