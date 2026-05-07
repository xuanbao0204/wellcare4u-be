package vn.wellcare4u.models.dto.doctor;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorExceptionDTO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private String reason;
}