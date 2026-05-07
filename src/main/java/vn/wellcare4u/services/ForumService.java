package vn.wellcare4u.services;

import org.springframework.data.domain.Page;

import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.models.request.CreateCommentRequest;
import vn.wellcare4u.models.request.PostRequest;

public interface ForumService {

	void deleteComment(Long commentId, String accountId);

	CommentDTO addComment(Long postId, CreateCommentRequest request, String accountId);

	PostDetailDTO likePost(Long postId);

	void deletePost(Long postId, String accountId);

	PostDetailDTO updatePost(Long postId, PostRequest request, String accountId);

	Page<PostSummaryDTO> getAllPosts(int page, int size, ESpecialization category, String keyword, EPostSortType sort);

	PostDetailDTO getPost(Long postId);

	PostDetailDTO createPost(PostRequest request, String accountId);

}
