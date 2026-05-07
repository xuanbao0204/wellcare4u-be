package vn.wellcare4u.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.wellcare4u.entities.ForumComment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.models.dto.forum.AuthorDTO;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;

@Component
public class ForumMapper {

 public PostSummaryDTO toSummary(ForumPost post) {
     return PostSummaryDTO.builder()
         .id(post.getId())
         .title(post.getTitle())
         .contentPreview(truncate(post.getContent(), 200))
         .category(post.getCategory())
         .author(toAuthorDTO(post.getAuthor(), post.isAnonymous()))
         .isAnonymous(post.isAnonymous())
         .isVerifiedAnswer(post.isVerifiedAnswer())
         .viewCount(post.getViewCount())
         .likes(post.getLikes())
         .commentCount(post.getComments() != null ? post.getComments().size() : 0)
         .tags(post.getTags())
         .createdAt(post.getCreatedAt())
         .build();
 }

 public PostDetailDTO toDetail(ForumPost post) {
     List<CommentDTO> topLevel = post.getComments().stream()
         .filter(c -> c.getParentComment() == null)
         .map(this::toCommentDTO)
         .collect(Collectors.toList());

     return PostDetailDTO.builder()
         .id(post.getId())
         .title(post.getTitle())
         .content(post.getContent())
         .category(post.getCategory())
         .author(toAuthorDTO(post.getAuthor(), post.isAnonymous()))
         .isAnonymous(post.isAnonymous())
         .isVerifiedAnswer(post.isVerifiedAnswer())
         .viewCount(post.getViewCount())
         .likes(post.getLikes())
         .tags(post.getTags())
         .comments(topLevel)
         .createdAt(post.getCreatedAt())
         .build();
 }

 public CommentDTO toCommentDTO(ForumComment comment) {
     List<CommentDTO> replies = comment.getPost().getComments().stream()
         .filter(c -> c.getParentComment() != null &&
                      c.getParentComment().getId().equals(comment.getId()))
         .map(this::toCommentDTO)
         .collect(Collectors.toList());

     return CommentDTO.builder()
         .id(comment.getId())
         .content(comment.getContent())
         .author(toAuthorDTO(comment.getAuthor(), false))
         .parentCommentId(comment.getParentComment() != null
             ? comment.getParentComment().getId() : null)
         .isExpertReply(comment.isExpertReply())
         .createdAt(comment.getCreatedAt())
         .replies(replies)
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