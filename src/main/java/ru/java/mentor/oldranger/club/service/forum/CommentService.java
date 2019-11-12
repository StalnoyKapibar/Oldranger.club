package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface CommentService {
    void createComment(Comment comment);

    void createComment(Comment comment, Topic topic);

    Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable);

    CommentDto assembleCommentDto(Comment comment);

    Page<CommentDto> getPageableCommentDtoByTopic(Topic topic, Pageable pageable);

    List<Comment> getAllComments();

    List<Comment> getAllCommentsByTopicId(Long id);

    Comment getCommentById(Long id);
}