package vn.wellcare4u.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemDTO {
    private String drug;
    private String dosage;
    private String frequency;
    private String duration;
    private String instruction;
}