package vn.wellcare4u.models.dto.forum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDTO {
    private Long id;
    private String displayName;
    private String avatar;
    private boolean isDoctor;
    private boolean isVerifiedDoctor;
}