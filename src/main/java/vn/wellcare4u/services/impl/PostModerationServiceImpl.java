package vn.wellcare4u.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.ForumPostModeration;
import vn.wellcare4u.enums.EForumPostEventType;
import vn.wellcare4u.enums.EPostStatus;
import vn.wellcare4u.events.ForumPostEvent;
import vn.wellcare4u.mapper.JsonParser;
import vn.wellcare4u.models.dto.forum.ModerationResultDTO;
import vn.wellcare4u.repositories.forum.ForumPostModerationRepository;
import vn.wellcare4u.services.AIToolService;
import vn.wellcare4u.services.PostModerationService;

@Service
@RequiredArgsConstructor
public class PostModerationServiceImpl implements PostModerationService {

	private final ForumPostModerationRepository modRepo;
	private final ApplicationEventPublisher publisher;
	private final AIToolService aiTools;

	@Override
	public ModerationResultDTO checkViolation(ForumPost post) {

	    String raw = aiTools.checkViolation(post);

	    ModerationResultDTO result =
	            JsonParser.parse(raw, ModerationResultDTO.class);

	    post.setStatus(
	            result.isViolating()
	                    ? EPostStatus.HIDDEN
	                    : EPostStatus.PUBLISHED);

	    saveModeration(post, result, raw);

	    publisher.publishEvent(
	            ForumPostEvent.builder()
	                    .type(result.isViolating()
	                            ? EForumPostEventType.BANNED
	                            : EForumPostEventType.PUBLISHED)
	                    .post(post)
	                    .build());

	    return result;
	}
	
	@Override
	public ModerationResultDTO getViolation(ForumPost post) {
		
		ForumPostModeration mod = modRepo.findByPost(post);
		return mapToDTO(mod);
	}
	
	@Override
	@Transactional
	public void saveModeration(ForumPost post, ModerationResultDTO result, String rawResponse) {
	    try {
	    	ForumPostModeration moderation = modRepo.findByPost(post);
	        if (moderation == null) {
	            moderation = new ForumPostModeration();
	            moderation.setPost(post);
	        }
	        moderation.setViolating(result.isViolating());
	        moderation.setConfidence(result.getConfidence());
	        moderation.setReason(result.getReason());
	        moderation.setCheckedAt(LocalDateTime.now());
	        moderation.setViolationCategories(JsonParser.toJson(result.getCategories()));
	        moderation.setViolatingContents(JsonParser.toJson(result.getViolatingContent()));
	        moderation.setSeverity(result.getSeverity());
	        modRepo.save(moderation);
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to save moderation result", e);
	    }
	}
	
	private ModerationResultDTO mapToDTO(ForumPostModeration mod) {
		return ModerationResultDTO.builder()
				.categories(JsonParser.fromJson(mod.getViolationCategories(), new TypeReference<List<String>>() {}))
				.confidence(mod.getConfidence())
				.isViolating(mod.isViolating())
				.medicalEmergency(mod.isMedicalEmergency())
				.reason(mod.getReason())
				.recommendedAction(mod.getRecommendedAction() != null ? mod.getRecommendedAction().name() : null)
				.severity(mod.getSeverity())
				.violatingContent(JsonParser.fromJson(mod.getViolatingContents(), new TypeReference<List<String>>() {}))
				.build();
	}
}
