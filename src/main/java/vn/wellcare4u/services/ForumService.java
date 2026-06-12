package vn.wellcare4u.services;

import org.springframework.data.domain.Page;

import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostManageDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.models.request.CreateCommentRequest;
import vn.wellcare4u.models.request.PostRequest;

public interface ForumService {

	void deleteComment(Long commentId, String accountId);

	CommentDTO addComment(Long postId, CreateCommentRequest request, String accountId);

	void deletePost(Long postId, String accountId);

	PostDetailDTO updatePost(Long postId, PostRequest request, String accountId);

	PostDetailDTO getPost(Long postId, String accountEmail);

	PostDetailDTO createPost(PostRequest request, String accountId);

	PostDetailDTO likePost(Long postId, String accountEmail);

	Page<PostSummaryDTO> getAllPosts(int page, int size, EForumCategory category, ESpecialization specialization,
			String keyword, EPostSortType sort);

	Page<PostManageDTO> getAllPostsByUserId(int page, int size, EForumCategory category, ESpecialization specialization, String keyword, EPostSortType sort,
			Long userId);

	Page<PostManageDTO> getAllPostsManage(int page, int size, EForumCategory category, ESpecialization specialization, String keyword, EPostSortType sort);

}
