package ru.oldranger.club.dao.ForumRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.comment.Comment;
import ru.oldranger.club.model.forum.Topic;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAll();

    Page<Comment> findByTopic(Topic topic, Pageable pageable);

    Page<Comment> findByUser(User user, Pageable pageable);

    List<Comment> findByTopicId(Long id);

    List<Comment> findByPositionGreaterThanAndTopicId(Long position, Long id);

}