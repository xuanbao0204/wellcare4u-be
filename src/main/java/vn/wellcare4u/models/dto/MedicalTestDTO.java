package vn.wellcare4u.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalTestDTO {
    private String testName;
    private String resultText;
    private String conclusion;
    private String imageUrl;
}
