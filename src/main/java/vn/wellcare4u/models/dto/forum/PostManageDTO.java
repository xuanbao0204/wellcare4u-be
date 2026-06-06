package vn.wellcare4u.models.dto.forum;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.ESpecialization;

@Data
@Builder
public class PostManageDTO {
    private Long id;
    private String title;
    private String content;
    private ESpecialization relatedSpecialization;
    private EForumCategory category;
    private AuthorDTO author;
    private boolean isAnonymous;
    private long viewCount;
    private long likes;
    private long commentCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private String status;
    private ModerationResultDTO moderationResult;
}