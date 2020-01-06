package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.forum.ImageComment;

public interface ImageCommentRepository extends JpaRepository<ImageComment, Long> {
}
