package vn.wellcare4u.controllers.user;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.models.request.CreateCommentRequest;
import vn.wellcare4u.models.request.PostRequest;
import vn.wellcare4u.services.ForumService;

@RestController
@RequestMapping("/api/v1/forum")
@RequiredArgsConstructor
public class ForumAPI {

	private final ForumService forumService;

	@GetMapping("/posts")
	public ApiResponse<Page<PostSummaryDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, 
			@RequestParam(required = false) ESpecialization category,
			@RequestParam(required = false) String keyword, 
			@RequestParam(defaultValue = "NEWEST") EPostSortType sort) {
		return ApiResponse.<Page<PostSummaryDTO>>builder()
				.status(200)
				.message("Get posts successfully")
				.data(forumService.getAllPosts(page, size, category, keyword, sort))
				.build();
	}

	@GetMapping("/posts/{postId}")
	public ApiResponse<PostDetailDTO> getPost(@PathVariable Long postId) {
		return ApiResponse.<PostDetailDTO>builder()
				.status(200)
				.message("Get posts successfully")
				.data(forumService.getPost(postId))
				.build();
	}

	@PostMapping("/posts")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<PostDetailDTO> createPost(@Valid @RequestBody PostRequest request,
			Authentication auth) {
		
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<PostDetailDTO>builder()
            		.status(401)
            		.errorCode("NOT_AUTHORIZED")
            		.message("Unauthorized access")
            		.build();
		}
		
		String accountId = auth.getName();
		return ApiResponse.<PostDetailDTO>builder()
				.status(HttpStatus.CREATED.value())
				.message("Get post detail successfully")
				.data(forumService.createPost(request, accountId))
				.build();
	}

	@PutMapping("/posts/{postId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<PostDetailDTO> updatePost(@PathVariable Long postId,
			@Valid @RequestBody PostRequest request, Authentication auth) {
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<PostDetailDTO>builder()
            		.status(401)
            		.errorCode("NOT_AUTHORIZED")
            		.message("Unauthorized access")
            		.build();
		}
		
		String accountId = auth.getName();
		return ApiResponse.<PostDetailDTO>builder()
				.status(HttpStatus.CREATED.value())
				.message("Get post detail successfully")
				.data(forumService.updatePost(postId, request, accountId))
				.build();
	}

	@DeleteMapping("/posts/{postId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<Void> deletePost(@PathVariable Long postId, Authentication auth) {
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<Void>builder()
            		.status(401)
            		.errorCode("NOT_AUTHORIZED")
            		.message("Unauthorized access")
            		.build();
		}
		
		String accountId = auth.getName();
		forumService.deletePost(postId, accountId);
		return ApiResponse.<Void>builder()
				.status(HttpStatus.CREATED.value())
				.message("Delete post detail successfully")
				.build();

	}

	@PostMapping("/posts/{postId}/like")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<PostDetailDTO> likePost(@PathVariable Long postId) {
		return ApiResponse.<PostDetailDTO>builder()
				.status(HttpStatus.CREATED.value())
				.message("Like post successfully")
				.data(forumService.likePost(postId))
				.build();
	}

	@PostMapping("/posts/{postId}/comments")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<CommentDTO> addComment(@PathVariable Long postId,
			@Valid @RequestBody CreateCommentRequest request, Authentication auth) {
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<CommentDTO>builder()
            		.status(401)
            		.errorCode("NOT_AUTHORIZED")
            		.message("Unauthorized access")
            		.build();
		}
		String accountId = auth.getName();
		return ApiResponse.<CommentDTO>builder()
				.status(HttpStatus.CREATED.value())
				.message("Comment successfully")
				.data(forumService.addComment(postId, request, accountId))
				.build();
	}

	@DeleteMapping("/comments/{commentId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<Void> deleteComment(@PathVariable Long commentId,
			Authentication auth) {
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<Void>builder()
            		.status(401)
            		.errorCode("NOT_AUTHORIZED")
            		.message("Unauthorized access")
            		.build();
		}
		String accountId = auth.getName();
		forumService.deleteComment(commentId, accountId);
		return ApiResponse.<Void>builder()
				.status(HttpStatus.CREATED.value())
				.message("Delete comment successfully")
				.build();
	}
}