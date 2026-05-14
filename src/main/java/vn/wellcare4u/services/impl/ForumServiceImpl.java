package vn.wellcare4u.services.impl;

import java.util.ArrayList;
import java.util.List;

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
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.EPostStatus;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.mapper.ForumMapper;
import vn.wellcare4u.models.dto.forum.CommentDTO;
import vn.wellcare4u.models.dto.forum.PostDetailDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.models.request.CreateCommentRequest;
import vn.wellcare4u.models.request.PostRequest;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.repositories.forum.ForumCommentRepository;
import vn.wellcare4u.repositories.forum.ForumPostRepository;
import vn.wellcare4u.services.ForumService;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

 private final ForumPostRepository postRepo;
 private final ForumCommentRepository commentRepo;
 private final ForumMapper mapper;
 private final UserRepository userRepo;

	 @Override
	 @Transactional
	 public PostDetailDTO createPost(PostRequest request, String accountEmail) {
	     
	     User author = userRepo.findByAccount_Email(accountEmail).orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
	     if (author == null) throw new IllegalStateException("Account has no user profile");
	
	     ForumPost post = new ForumPost();
	     post.setTitle(request.getTitle());
	     post.setContent(request.getContent());
	     post.setCategory(request.getCategory());
	     post.setAnonymous(request.isAnonymous());
	     post.setTags(request.getTags() != null ? request.getTags() : List.of());
	     post.setAuthor(author);
	     post.setComments(new ArrayList<>());
	     post.setViewCount(0);
	     post.setLikes(0);
	     post.setVerifiedAnswer(false);
	     post.setStatus(EPostStatus.PUBLIC);
	
	     return mapper.toDetail(postRepo.save(post));
	 }
	
	 @Override
	 @Transactional
	 public PostDetailDTO getPost(Long postId) {
	     ForumPost post = findPostOrThrow(postId);
	     post.setViewCount(post.getViewCount() + 1);
	     return mapper.toDetail(postRepo.save(post));
	 }
	
	 @Override
	 public Page<PostSummaryDTO> getAllPosts(int page, int size,
	                                               ESpecialization category,
	                                               String keyword,
	                                               EPostSortType sort) {
	     // Build sort strategy
	     Sort sorting = switch (sort) {
	         case MOST_LIKED     -> Sort.by("likes").descending();
	         case MOST_VIEWED    -> Sort.by("viewCount").descending();
	         case MOST_COMMENTED -> Sort.by("comments.size").descending();
	         default             -> Sort.by("createdAt").descending(); // NEWEST
	     };
	
	     Pageable pageable = PageRequest.of(page, size, sorting);
	
	     Page<ForumPost> posts;
	     if (keyword != null && !keyword.isBlank()) {
	         posts = postRepo.searchWithFilter(keyword, category, pageable);
	     } else if (category != null) {
	         posts = postRepo.findByCategory(category, pageable);
	     } else {
	         posts = postRepo.findAll(pageable);
	     }
	
	     return posts.map(mapper::toSummary);
	 }
	
	 @Override
	 @Transactional
	 public PostDetailDTO updatePost(Long postId, PostRequest request, String accountId) {
	     ForumPost post = findPostOrThrow(postId);
	     validateOwnership(post.getAuthor(), accountId);
	
	     if (request.getTitle()    != null) post.setTitle(request.getTitle());
	     if (request.getContent()  != null) post.setContent(request.getContent());
	     if (request.getCategory() != null) post.setCategory(request.getCategory());
	     if (request.getTags()     != null) post.setTags(request.getTags());
	
	     return mapper.toDetail(postRepo.save(post));
	 }
	
	 @Override
	 @Transactional
	 public void deletePost(Long postId, String accountEmail) {
	     ForumPost post = findPostOrThrow(postId);
	     User author = userRepo.findByAccount_Email(accountEmail).orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
	
	     boolean isAdmin  = author.getAccount().getRole() == ERole.ADMIN;
	     boolean isAuthor = post.getAuthor().getAccount().getEmail().equals(accountEmail);
	
	     if (!isAdmin && !isAuthor) {
	         throw new AppException("You do not have permission", "NO_PERMISSION", HttpStatus.BAD_REQUEST);
	     }
	
	     postRepo.delete(post);
	 }
	
	 @Override
	 @Transactional
	 public PostDetailDTO likePost(Long postId) {
	     ForumPost post = findPostOrThrow(postId);
	     post.setLikes(post.getLikes() + 1);
	     return mapper.toDetail(postRepo.save(post));
	 }
	
	 @Override
	 @Transactional
	 public CommentDTO addComment(Long postId, CreateCommentRequest request, String accountEmail) {
	     ForumPost post = findPostOrThrow(postId);
	     User author = userRepo.findByAccount_Email(accountEmail).orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
	     boolean isExpert = author instanceof Doctor && ((Doctor) author).isVerified();
	
	     ForumComment comment = new ForumComment();
	     comment.setContent(request.getContent());
	     comment.setPost(post);
	     comment.setAuthor(author);
	     comment.setExpertReply(isExpert);
	
	     if (request.getParentCommentId() != null) {
	         ForumComment parent = commentRepo.findById(request.getParentCommentId())
	             .orElseThrow(() -> new AppException("Parent comment not found", "PARENT_NOT_FOUND", HttpStatus.NOT_FOUND));
	         if (!parent.getPost().getId().equals(postId)) {
	             throw new IllegalArgumentException("Parent comment does not belong to this post");
	         }
	         comment.setParentComment(parent);
	     }
	
	     ForumComment saved = commentRepo.save(comment);
	     if (isExpert) {
	         post.setVerifiedAnswer(true);
	         postRepo.save(post);
	     }
	
	     return mapper.toCommentDTO(saved);
	 }
	
	 @Override
	 @Transactional
	 public void deleteComment(Long commentId, String accountEmail) {
	     ForumComment comment = commentRepo.findById(commentId)
	         .orElseThrow(() -> new AppException("Comment not found", "COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND));
	     User author = userRepo.findByAccount_Email(accountEmail).orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));
	     boolean isAdmin  = author.getAccount().getRole() == ERole.ADMIN;
	     boolean isAuthor = comment.getAuthor().getAccount().getEmail().equals(accountEmail);
	
	     if (!isAdmin && !isAuthor)
	         throw new AppException("You do not have permission", "NO_PERMISSION", HttpStatus.BAD_REQUEST);
	
	     commentRepo.delete(comment);
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