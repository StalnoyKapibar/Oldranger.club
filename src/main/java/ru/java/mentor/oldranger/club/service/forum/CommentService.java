package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface CommentService {
    void createComment(Comment comment);

    Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable);
}
