package vn.wellcare4u.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.enums.EForumPostEventType;

@Builder
@Getter
@AllArgsConstructor
public class ForumPostEvent {

	private EForumPostEventType type;
	private ForumPost post;
	private String actor;
	
}
