package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.CommentDtoAndCountMessages;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface CommentService {
    void createComment(Comment comment);

    void deleteComment(Long id);

    void updateComment(Comment comment);

    Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable);

    CommentDto assembleCommentDto(Comment comment, User user);

    CommentDtoAndCountMessages assembleCommentDtoAndMessages(List<CommentDto> commentDto, Long countMessages);

    Page<CommentDto> getPageableCommentDtoByTopic(Topic topic, Pageable pageable, int position, User user);

    Page<CommentDto> getPageableCommentDtoByUser(User user, Pageable pageable);

    List<Comment> getAllComments();

    List<Comment> getAllCommentsByTopicId(Long id);

    Comment getCommentById(Long id);

    void updatePostion(Long topicID, Long deletedPosition);

    boolean isEmptyComment(String comment);

    List<Comment> getChildComment(Comment comment);
}