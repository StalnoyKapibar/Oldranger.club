package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.CommentRepository;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private UserStatisticService userStatisticService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserStatisticService userStatisticService) {
        this.commentRepository = commentRepository;
        this.userStatisticService = userStatisticService;
    }

    @Override
    public void createComment(Comment comment) {
        commentRepository.save(comment);
        UserStatistic userStatistic = userStatisticService.getUserStaticById(comment.getUser().getId());
        if (userStatistic == null) {
            userStatistic = new UserStatistic(comment.getUser());

        }
        long messages = userStatistic.getMessageCount();
        userStatistic.setMessageCount(++messages);
        userStatisticService.saveUserStatic(userStatistic);

    }

    @Override
    public Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable) {
        return commentRepository.findByTopic(topic, pageable);
    }

    public Page<CommentDto> getPageableCommentDtoByTopic(Topic topic, Pageable pageable) {
        return commentRepository.findByTopic(topic, pageable).map(this::assembleCommentDto);
    }


    public CommentDto assembleCommentDto(Comment comment) {

        CommentDto commentDto = new CommentDto();

        LocalDateTime replyTime = null;
        String replyNick = null;
        String replyText = null;

        try {
            replyTime = comment.getAnswerTo().getDateTime();
        } catch (NullPointerException e) {
            //
        }
        try {
            replyNick = comment.getAnswerTo().getUser().getNickName();
        } catch (NullPointerException e) {
            //
        }
        try {
            replyText = comment.getAnswerTo().getCommentText();
        } catch (NullPointerException e) {
            //
        }

        commentDto.setNickName(comment.getUser().getNickName());
        commentDto.setRoleName(comment.getUser().getRole().getRole());
        commentDto.setSmallAvatar(comment.getUser().getAvatar().getSmall());
        commentDto.setCommentDateTime(comment.getDateTime());
        commentDto.setMessageCount(userStatisticService.getUserStaticById(comment.getUser().getId()).getMessageCount());
        commentDto.setTimeSinceRegistration(timeSinceRegistration(comment.getUser().getRegDate()));
        commentDto.setReplyDateTime(replyTime);
        commentDto.setReplyNick(replyNick);
        commentDto.setReplyText(replyText);
        commentDto.setCommentText(comment.getCommentText());
        return commentDto;
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }


    private String timeSinceRegistration(LocalDateTime regDate) {
        Duration dur = Duration.between(regDate, LocalDateTime.now());
        long days = dur.toDays();
        return days < 365 ? "С нами уже " + days + " день(-ей)" :
                "С нами больше " + ChronoUnit.YEARS.between(LocalDateTime.now(), LocalDateTime.now().plus(dur)) + " лет";
    }


    @Override
    public List<Comment> getAllCommentsByTopicId(Long id) {
        return commentRepository.findByTopicId(id);
    }

    @Override
    public Comment getCommentById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElseThrow(() -> new RuntimeException("not found comment by id: " + id));
    }
}