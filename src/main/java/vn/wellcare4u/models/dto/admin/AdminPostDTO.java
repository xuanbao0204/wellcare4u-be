package vn.wellcare4u.models.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostDTO {

    private Long id;
    private String title;
    private String contentPreview;
    private String category;
    private String authorName;
    private String authorEmail;
    private boolean isAnonymous;
    private boolean isVerifiedAnswer;
    private int viewCount;
    private long likes;
    private int commentCount;
    private List<String> tags;
    private String createdAt;
}