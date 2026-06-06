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
    private ESpecialization relatedSpecialization;
    private String category;
    private AuthorDTO author;
    private boolean isAnonymous;
    private long viewCount;
    private long likes;
    private long commentCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private String status;
    private boolean isViolatingContent;
}