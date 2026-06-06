package vn.wellcare4u.models.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.ESpecialization;

@Data
public class PostRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000)
    private String content;

    private ESpecialization relatedSpecialization;
    private EForumCategory category;
    private boolean isAnonymous;
    private boolean allowComment;
    private List<String> tags;
}