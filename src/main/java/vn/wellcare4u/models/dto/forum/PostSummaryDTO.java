package vn.wellcare4u.models.dto.forum;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import vn.wellcare4u.enums.ESpecialization;

@Data
@Builder
public class PostSummaryDTO {
    private Long id;
    private String title;
    private String contentPreview;
    private ESpecialization category;
    private AuthorDTO author;
    private boolean isAnonymous;
    private boolean isVerifiedAnswer;
    private int viewCount;
    private long likes;
    private int commentCount;
    private List<String> tags;
    private LocalDateTime createdAt;
}