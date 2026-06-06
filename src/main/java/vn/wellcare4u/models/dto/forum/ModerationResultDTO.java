package vn.wellcare4u.models.dto.forum;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.enums.EModerationSeverity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModerationResultDTO {

	@JsonProperty("isViolating")
    private boolean isViolating;

    private EModerationSeverity severity;

    private Double confidence;

    private String reason;

    private List<String> violatingContent;

    private List<String> categories;

    private String recommendedAction;

    private boolean medicalEmergency;
}