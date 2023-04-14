package ru.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oldranger.club.dto.CommentDto;
import ru.oldranger.club.model.comment.Comment;
import ru.oldranger.club.model.forum.Topic;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface CommentService {
    void createComment(Comment comment);

    void deleteComment(Long id);

    void updateComment(Comment comment);

    Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable);

    CommentDto assembleCommentDto(Comment comment, User user);

    Page<CommentDto> getPageableCommentDtoByTopic(Topic topic, Pageable pageable, int position, User user);

    Page<CommentDto> getPageableCommentDtoByUser(User user, Pageable pageable);

    List<Comment> getAllComments();

    List<Comment> getAllCommentsByTopicId(Long id);

    Comment getCommentById(Long id);

    void updatePostion(Long topicID, Long deletedPosition);
}