package vn.wellcare4u.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.enums.ENotificationType;
import vn.wellcare4u.events.ForumPostEvent;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.services.NotificationService;

@Component
@RequiredArgsConstructor
public class ForumPostNotificationListener {

	private final NotificationService notiServ;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ForumPostEvent event) {

		try {

			switch (event.getType()) {
			case PUBLISHED -> handlePublished(event);
			case PENDING_REVIEW -> handlePendingReview(event);
			case NEW_LIKE -> handleNewLike(event);
			case NEW_COMMENT -> handleNewComment(event);
			case BANNED -> handleBanned(event);
			case DELETED -> handleDeleted(event);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void handlePublished(ForumPostEvent event) {
		ForumPost post = event.getPost();

		notiServ.send(NotificationRequest.toUser(post.getAuthor().getId(), ENotificationType.INFO, "Bài viết",
				"Bài viết của bạn đã được đăng. Mã bài viết: #" + post.getId(), null));
	}

	private void handlePendingReview(ForumPostEvent event) {
		ForumPost post = event.getPost();

		notiServ.send(NotificationRequest.toUser(post.getAuthor().getId(), ENotificationType.INFO, "Bài viết",
				"Bài viết của bạn đang được xử lý và kiểm duyệt. Mã bài viết: #" + post.getId(), null));
	}

	private void handleNewLike(ForumPostEvent event) {
		ForumPost post = event.getPost();

		notiServ.send(NotificationRequest.toUser(post.getAuthor().getId(), ENotificationType.INFO, "Bài viết",
				"Bài viết của bạn có lượt thích mới. Mã bài viết: #" + post.getId(), null));
	}

	private void handleNewComment(ForumPostEvent event) {
		ForumPost post = event.getPost();

		notiServ.send(NotificationRequest.toUser(post.getAuthor().getId(), ENotificationType.INFO, "Bài viết",
				"Bài viết của bạn có bình luận mới. Mã bài viết: #" + post.getId(), null));
	}

	private void handleBanned(ForumPostEvent event) {
		ForumPost post = event.getPost();

		notiServ.send(NotificationRequest.toUser(post.getAuthor().getId(), ENotificationType.INFO, "Bài viết",
				"Bài viết của bạn đã bị giữ lại vì đang có dấu hiệu vi phạm. Hãy kiểm tra và chỉnh sửa lại. Mã bài viết: #" + post.getId(), null));
	}

	private void handleDeleted(ForumPostEvent event) {
		ForumPost post = event.getPost();

		notiServ.send(NotificationRequest.toUser(post.getAuthor().getId(), ENotificationType.INFO, "Bài viết",
				"Bài viết của bạn đã được xóa thành công.", null));
	}
}
