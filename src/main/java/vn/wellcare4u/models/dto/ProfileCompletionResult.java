package vn.wellcare4u.models.dto;

import java.util.List;

public record ProfileCompletionResult(
        boolean completed,
        int percentage,
        List<String> missingFields
) {
}