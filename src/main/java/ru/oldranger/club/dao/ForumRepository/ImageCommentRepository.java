package ru.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.forum.ImageComment;

import java.util.List;

public interface ImageCommentRepository extends JpaRepository<ImageComment, Long> {

    List<ImageComment> findAllByCommentId(Long commentId);
}
