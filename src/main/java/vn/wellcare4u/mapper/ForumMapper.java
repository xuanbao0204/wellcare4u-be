package vn.wellcare4u.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import vn.wellcare4u.entities.ForumComment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.models.dto.forum.AuthorDTO;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostManageDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;

@Component
public class ForumMapper {

	public PostSummaryDTO toSummary(ForumPost post) {

		return PostSummaryDTO.builder()
				.id(post.getId())
				.title(post.getTitle())
				.contentPreview(truncate(post.getContent(), 200))
				.relatedSpecialization(
						post.getRelatedSpecialization() != null ? post
								.getRelatedSpecialization(): null)
				.category(post.getCategory().getValue())
				.author(toAuthorDTO(post.getAuthor(), post.isAnonymous()))
				.isAnonymous(post.isAnonymous())
				.viewCount(post.getViewCount())
				.likes(post.getLikes())
				.commentCount(post.getCommentCount())
				.tags(post.getTags())
				.createdAt(post.getCreatedAt())
				.status(post.getStatus().getValue())
				.build();
	}

	public PostManageDTO toPostManage(ForumPost post) {
		return PostManageDTO.builder()
				.id(post.getId())
				.title(post.getTitle())
				.content(post.getContent())
				.relatedSpecialization(
						post.getRelatedSpecialization() != null ? post
								.getRelatedSpecialization(): null)
				.category(post.getCategory())
				.author(toAuthorDTO(post.getAuthor(), post.isAnonymous()))
				.isAnonymous(post.isAnonymous())
				.viewCount(post.getViewCount())
				.likes(post.getLikes())
				.commentCount(post.getCommentCount())
				.tags(post.getTags())
				.createdAt(post.getCreatedAt())
				.status(post.getStatus().getValue())
				.build();
	}
	
	public PostDetailDTO toDetail(ForumPost post) {

	    return PostDetailDTO.builder()
	            .id(post.getId())
	            .title(post.getTitle())
	            .content(post.getContent())
	            .relatedSpecialization(
	                    post.getRelatedSpecialization() != null
	                            ? post.getRelatedSpecialization()
	                            : null
	            )
	            .category(
	                    post.getCategory() != null
	                            ? post.getCategory().getValue()
	                            : null
	            )
	            .author(
	                    toAuthorDTO(
	                            post.getAuthor(),
	                            post.isAnonymous()
	                    )
	            )
	            .isAnonymous(post.isAnonymous())
	            .viewCount(post.getViewCount())
	            .likes(post.getLikes())
	            .commentCount(post.getCommentCount())
	            .allowComment(post.isAllowComment())
	            .tags(post.getTags())
	            .createdAt(post.getCreatedAt())
	            .status(
	                    post.getStatus() != null
	                            ? post.getStatus().getValue()
	                            : null
	            )
	            .build();
	}

	public CommentDTO toCommentDTO(ForumComment comment) {

	    return CommentDTO.builder()
	            .id(comment.getId())
	            .content(comment.getContent())
	            .author(
	                    toAuthorDTO(
	                            comment.getAuthor(),
	                            false
	                    )
	            )
	            .parentCommentId(
	                    comment.getParentComment() != null
	                            ? comment.getParentComment().getId()
	                            : null
	            )
	            .isExpertReply(
	                    comment.isExpertReply()
	            )
	            .createdAt(
	                    comment.getCreatedAt()
	            )
	            .replies(new ArrayList<>())
	            .build();
	}

 private AuthorDTO toAuthorDTO(User user, boolean isAnonymous) {
     if (isAnonymous) {
         return AuthorDTO.builder()
             .displayName("Anonymous")
             .isDoctor(false)
             .isVerifiedDoctor(false)
             .build();
     }
     boolean isDoctor = user instanceof Doctor;
     return AuthorDTO.builder()
         .id(user.getId())
         .displayName(user.getFirstName() + " " + user.getLastName())
         .avatar(user.getAvatar())
         .isDoctor(isDoctor)
         .isVerifiedDoctor(isDoctor && ((Doctor) user).isVerified())
         .build();
 }

 private String truncate(String text, int maxLength) {
     if (text == null) return "";
     return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
 }
}