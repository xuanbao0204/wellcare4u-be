package vn.wellcare4u.services;

import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.models.dto.forum.ModerationResultDTO;

public interface PostModerationService {

	void saveModeration(ForumPost post, ModerationResultDTO result, String rawResponse);

	ModerationResultDTO checkViolation(ForumPost post);

	ModerationResultDTO getViolation(ForumPost post);

}
