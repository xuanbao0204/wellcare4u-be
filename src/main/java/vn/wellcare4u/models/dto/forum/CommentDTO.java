package vn.wellcare4u.models.dto.forum;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private AuthorDTO author;
    private Long parentCommentId;
    private boolean isExpertReply;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies;
}