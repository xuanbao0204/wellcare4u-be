package vn.wellcare4u.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Content is required")
    @Size(max = 3000)
    private String content;

    private Long parentCommentId;
}