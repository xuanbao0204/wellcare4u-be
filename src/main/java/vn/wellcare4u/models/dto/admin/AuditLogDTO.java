package vn.wellcare4u.models.dto.admin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.UserDTO;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuditLogDTO {
	private Long id;
    private UserDTO actor;
    private String action;
    private String entityType;
    private Long entityId;
    private LocalDateTime timestamp;
}
