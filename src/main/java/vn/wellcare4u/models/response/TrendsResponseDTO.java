package vn.wellcare4u.models.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.DashboardTrendPointDTO;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TrendsResponseDTO {
    private String periodLabel;
    private boolean hasPrev;
    private boolean hasNext;
    private List<DashboardTrendPointDTO> trends;
}