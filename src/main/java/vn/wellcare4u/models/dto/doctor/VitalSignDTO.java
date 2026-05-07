package vn.wellcare4u.models.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalSignDTO {
    private Double height;
    private Double weight;
    private Double bmi;
    private String bloodPressure;
    private Integer heartRate;
    private Double bloodSugar;
}