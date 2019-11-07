package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    void createComment(Comment comment);

    List<Comment> getAllComments();

    List<Comment> getAllCommentsByTopicId(Long id);

    Comment getCommentById(Long id);
}