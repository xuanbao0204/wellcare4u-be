package vn.wellcare4u.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.ForumComment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.ForumPostLike;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.EForumCategory;
//import vn.wellcare4u.enums.EForumPostEventType;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.EPostStatus;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.enums.ESpecialization;
//import vn.wellcare4u.events.ForumPostEvent;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.mapper.ForumMapper;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.ModerationResultDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostManageDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.models.request.CreateCommentRequest;
import vn.wellcare4u.models.request.PostRequest;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.repositories.forum.ForumCommentRepository;
import vn.wellcare4u.repositories.forum.ForumPostLikeRepository;
import vn.wellcare4u.repositories.forum.ForumPostRepository;
import vn.wellcare4u.services.ForumService;
import vn.wellcare4u.services.PostModerationService;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

	private final ForumPostRepository postRepo;
	private final ForumCommentRepository commentRepo;
	private final ForumMapper mapper;
	private final UserRepository userRepo;
	private final ForumPostLikeRepository likeRepo;
//	private final ApplicationEventPublisher publisher;
	private final PostModerationService modServ;
	
	@Override
	@Transactional
	public PostDetailDTO createPost(PostRequest request, String accountEmail) {

		User author = userRepo.findByAccount_Email(accountEmail)
				.orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
		if (author == null)
			throw new IllegalStateException("Account has no user profile");

		ForumPost post = new ForumPost();

		post.setTitle(request.getTitle());

		post.setContent(request.getContent());

		post.setCategory(request.getCategory());

		post.setRelatedSpecialization(request.getRelatedSpecialization() != null ? request.getRelatedSpecialization() : null);

		post.setAnonymous(request.isAnonymous());

		post.setAllowComment(request.isAllowComment());

		post.setTags(request.getTags() != null ? request.getTags() : List.of());

		post.setAuthor(author);

		post.setViewCount(0);

		post.setLikes(0);

		post.setCommentCount(0);

		post.setStatus(EPostStatus.PENDING_REVIEW);
		
		post = postRepo.save(post);
		
		ModerationResultDTO check = modServ.checkViolation(post);
		
		PostDetailDTO dto = mapper.toDetail(post);
		
		dto.setViolationCheck(check);
		
		return dto;
	}
	
	@Override
	@Transactional
	public PostDetailDTO getPost(Long postId, String accountEmail) {

		ForumPost post = findPostOrThrow(postId);

		if(post.getStatus() == EPostStatus.DELETED) {
		    throw new AppException(
		        "Post not found",
		        "POST_NOT_FOUND",
		        HttpStatus.NOT_FOUND
		    );
		}
		
		post.setViewCount(post.getViewCount() + 1);

		postRepo.save(post);

		PostDetailDTO dto = mapper.toDetail(post);
		List<CommentDTO> comments =
		        commentRepo
		                .findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(
		                        postId
		                )
		                .stream()
		                .map(this::buildCommentTree)
		                .toList();

		dto.setComments(comments);

		if (accountEmail != null) {

			User user = userRepo.findByAccount_Email(accountEmail).orElse(null);

			if (user != null) {

				dto.setLikedByCurrentUser(likeRepo.existsByPost_IdAndUser_Id(postId, user.getId()));
			}
		}

		return dto;
	}

	private CommentDTO buildCommentTree(ForumComment comment) {

		CommentDTO dto = mapper.toCommentDTO(comment);

		List<CommentDTO> replies = commentRepo.findByParentCommentIdOrderByCreatedAtAsc(comment.getId()).stream()
				.map(this::buildCommentTree).toList();

		dto.setReplies(replies);

		return dto;
	}
	
	@Override
	public Page<PostSummaryDTO> getAllPosts(int page, int size, EForumCategory category, ESpecialization specialization,
			String keyword, EPostSortType sort) {

		Sort sorting = switch (sort) {
		case MOST_LIKED -> Sort.by("likes").descending();
		case MOST_VIEWED -> Sort.by("viewCount").descending();
		case MOST_COMMENTED -> Sort.by("commentCount").descending();
		default -> Sort.by("createdAt").descending(); // NEWEST
		};

		Pageable pageable = PageRequest.of(page, size, sorting);

		Page<ForumPost> posts;
		if (keyword != null && !keyword.isBlank()) {

			posts = postRepo.searchWithFilter(keyword, category, specialization, pageable);

		} else {

			posts = postRepo.filterPosts(category, specialization, pageable);
		}

		return posts.map(mapper::toSummary);
	}
	
	@Override
	public Page<PostManageDTO> getAllPostsManage(int page, int size, EForumCategory category, ESpecialization specialization,
			String keyword, EPostSortType sort) {

		Sort sorting = switch (sort) {
		case MOST_LIKED -> Sort.by("likes").descending();
		case MOST_VIEWED -> Sort.by("viewCount").descending();
		case MOST_COMMENTED -> Sort.by("commentCount").descending();
		default -> Sort.by("createdAt").descending(); // NEWEST
		};

		Pageable pageable = PageRequest.of(page, size, sorting);

		Page<ForumPost> posts;
		if (keyword != null && !keyword.isBlank()) {

			posts = postRepo.searchWithFilter(keyword, category, specialization, pageable);

		} else {

			posts = postRepo.filterPosts(category, specialization, pageable);
		}

		return posts.map(post -> {
	        PostManageDTO dto = mapper.toPostManage(post);

	        ModerationResultDTO check = modServ.getViolation(post);
	        dto.setModerationResult(check);

	        return dto;
	    });
	}
	
	@Override
	public Page<PostManageDTO> getAllPostsByUserId(int page, int size, EForumCategory category, ESpecialization specialization,
			String keyword, EPostSortType sort, Long userId) {

		Sort sorting = switch (sort) {
		case MOST_LIKED -> Sort.by("likes").descending();
		case MOST_VIEWED -> Sort.by("viewCount").descending();
		case MOST_COMMENTED -> Sort.by("commentCount").descending();
		default -> Sort.by("createdAt").descending(); // NEWEST
		};

		Pageable pageable = PageRequest.of(page, size, sorting);

		Page<ForumPost> posts;
		if (keyword != null && !keyword.isBlank()) {

			posts = postRepo.searchWithFilterAndUserId(keyword, category, specialization, userId, pageable);

		} else {

			posts = postRepo.filterPostsWithUserId(category, specialization, userId, pageable);
		}

		return posts.map(post -> {
	        PostManageDTO dto = mapper.toPostManage(post);

	        ModerationResultDTO check = modServ.getViolation(post);
	        dto.setModerationResult(check);

	        return dto;
	    });
	}

	@Override
	@Transactional
	public PostDetailDTO updatePost(Long postId, PostRequest request, String accountId) {
		ForumPost post = findPostOrThrow(postId);
		validateOwnership(post.getAuthor(), accountId);

		if (request.getTitle() != null)
			post.setTitle(request.getTitle());

		if (request.getContent() != null)
			post.setContent(request.getContent());

		if (request.getCategory() != null)
			post.setCategory(request.getCategory());

		post.setRelatedSpecialization(request.getRelatedSpecialization());

		if (request.getTags() != null)
			post.setTags(request.getTags());
		
		post.setAllowComment(request.isAllowComment());

		post.setUpdatedAt(LocalDateTime.now());
		
		post.setStatus(EPostStatus.PENDING_REVIEW);
		
//		post = postRepo.save(post);
		
		ModerationResultDTO check = modServ.checkViolation(post);
		
		PostDetailDTO dto = mapper.toDetail(post);
		
		dto.setViolationCheck(check);
		
		List<CommentDTO> comments =
		        commentRepo
		                .findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(
		                        postId
		                )
		                .stream()
		                .map(this::buildCommentTree)
		                .toList();

		dto.setComments(comments);
		
		return dto;
	}

	@Override
	@Transactional
	public void deletePost(Long postId, String accountEmail) {
		ForumPost post = findPostOrThrow(postId);
		User author = userRepo.findByAccount_Email(accountEmail)
				.orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

		boolean isAdmin = author.getAccount().getRole() == ERole.ADMIN;
		boolean isAuthor = post.getAuthor().getAccount().getEmail().equals(accountEmail);

		if (!isAdmin && !isAuthor) {
			throw new AppException("You do not have permission", "NO_PERMISSION", HttpStatus.BAD_REQUEST);
		}

		post.setStatus(EPostStatus.DELETED);

		post.setUpdatedAt(LocalDateTime.now());

		postRepo.save(post);
	}

	@Override
	@Transactional
	public PostDetailDTO likePost(Long postId, String accountEmail) {

		ForumPost post = findPostOrThrow(postId);

		User user = userRepo.findByAccount_Email(accountEmail)
				.orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

		Optional<ForumPostLike> existing = likeRepo.findByPost_IdAndUser_Id(postId, user.getId());

		if (existing.isPresent()) {

			likeRepo.delete(existing.get());

			post.setLikes(Math.max(0, post.getLikes() - 1));

		} else {

			ForumPostLike like = new ForumPostLike();

			like.setPost(post);
			like.setUser(user);

			likeRepo.save(like);

			post.setLikes(post.getLikes() + 1);
		}

		post.setUpdatedAt(LocalDateTime.now());

		postRepo.save(post);

		PostDetailDTO dto = mapper.toDetail(post);

		dto.setLikedByCurrentUser(!existing.isPresent());
		
		List<CommentDTO> comments =
		        commentRepo
		                .findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(
		                        postId
		                )
		                .stream()
		                .map(this::buildCommentTree)
		                .toList();

		dto.setComments(comments);

		return dto;
	}

	@Override
	@Transactional
	public CommentDTO addComment(Long postId, CreateCommentRequest request, String accountEmail) {

		ForumPost post = findPostOrThrow(postId);

		if (!post.isAllowComment()) {
			throw new AppException("Comment is disabled", "COMMENT_DISABLED", HttpStatus.BAD_REQUEST);
		}

		User author = userRepo.findByAccount_Email(accountEmail)
				.orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

		boolean isExpert = author instanceof Doctor && ((Doctor) author).isVerified();

		ForumComment comment = new ForumComment();

		comment.setContent(request.getContent());
		comment.setPost(post);
		comment.setAuthor(author);
		comment.setExpertReply(isExpert);

		if (request.getParentCommentId() != null) {

			ForumComment parent = commentRepo.findById(request.getParentCommentId())
					.orElseThrow(() -> new AppException("Parent comment not found", "PARENT_COMMENT_NOT_FOUND",
							HttpStatus.NOT_FOUND));

			if (!parent.getPost().getId().equals(postId)) {
				throw new AppException("Invalid parent comment", "INVALID_PARENT", HttpStatus.BAD_REQUEST);
			}

			comment.setParentComment(parent);
		}

		ForumComment saved = commentRepo.save(comment);

		post.setCommentCount(post.getCommentCount() + 1);

		post.setUpdatedAt(LocalDateTime.now());

		postRepo.save(post);

		return mapper.toCommentDTO(saved);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId, String accountEmail) {

		ForumComment comment = commentRepo.findById(commentId)
				.orElseThrow(() -> new AppException("Comment not found", "COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND));

		User user = userRepo.findByAccount_Email(accountEmail)
				.orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

		boolean isAdmin = user.getAccount().getRole() == ERole.ADMIN;

		boolean isAuthor = comment.getAuthor().getAccount().getEmail().equals(accountEmail);

		if (!isAdmin && !isAuthor) {
			throw new AppException("No permission", "NO_PERMISSION", HttpStatus.FORBIDDEN);
		}

		ForumPost post = comment.getPost();

		commentRepo.delete(comment);

		post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

		post.setUpdatedAt(LocalDateTime.now());

		postRepo.save(post);
	}

	private ForumPost findPostOrThrow(Long postId) {
		return postRepo.findById(postId)
				.orElseThrow(() -> new AppException("Post not found", "POST_NOT_FOUND", HttpStatus.NOT_FOUND));
	}

	private void validateOwnership(User author, String accountId) {
		if (!author.getAccount().getEmail().equals(accountId))
			throw new AppException("You do not have permission", "NO_PERMISSION", HttpStatus.BAD_REQUEST);
	}
}