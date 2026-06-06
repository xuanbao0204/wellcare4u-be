package vn.wellcare4u.models.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SuggestSpecializationDTO {
	private String userSymptom;
	private String suggestion;
	private String message;
	private double matchingRate;
}
