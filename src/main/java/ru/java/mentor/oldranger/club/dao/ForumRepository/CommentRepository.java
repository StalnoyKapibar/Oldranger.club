package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.forum.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAll();
}
