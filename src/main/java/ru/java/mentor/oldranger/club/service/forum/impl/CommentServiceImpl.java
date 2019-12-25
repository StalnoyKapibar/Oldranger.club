package ru.java.mentor.oldranger.club.service.forum.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.CommentRepository;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);
    private CommentRepository commentRepository;
    private UserStatisticService userStatisticService;
    private TopicService topicService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserStatisticService userStatisticService, TopicService topicService) {
        this.commentRepository = commentRepository;
        this.userStatisticService = userStatisticService;
        this.topicService = topicService;
    }

    @Override
    public void createComment(Comment comment) {
        LOG.info("Saving comment {}", comment);
        try {
            Topic topic = comment.getTopic();
            topic.setLastMessageTime(comment.getDateTime());
            long messages = topic.getMessageCount();
            comment.setPositionInTopic(++messages);
            topic.setMessageCount(messages);
            topicService.editTopicByName(topic);
            commentRepository.save(comment);
            UserStatistic userStatistic = userStatisticService.getUserStaticByUser(comment.getUser());
            messages = userStatistic.getMessageCount();
            userStatistic.setMessageCount(++messages);
            userStatistic.setLastComment(comment.getDateTime());
            userStatisticService.saveUserStatic(userStatistic);
            LOG.info("Comment saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable) {
        LOG.debug("Getting page {} of comments for topic with id = {}", pageable.getPageNumber(), topic.getId());
        Page<Comment> page = null;
        try {
            page = commentRepository.findByTopic(topic, pageable);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }

    public Page<CommentDto> getPageableCommentDtoByTopic(Topic topic, Pageable pageable) {
        LOG.debug("Getting page {} of comment dtos for topic with id = {}", pageable.getPageNumber(), topic.getId());
        Page<CommentDto> page = null;
        try {
            page = commentRepository.findByTopic(topic, pageable).map(this::assembleCommentDto);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public Page<CommentDto> getPageableCommentDtoByUser(User user, Pageable pageable) {
        LOG.debug("Getting page {} of comment dtos for user with id = {}", pageable.getPageNumber(), user.getId());
        Page<CommentDto> page = null;
        try {
            page = commentRepository.findByUser(user, pageable).map(this::assembleCommentDto);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }


    public CommentDto assembleCommentDto(Comment comment) {
        LOG.debug("Assembling comment {} dto", comment);
        CommentDto commentDto = new CommentDto();
        try {
            LocalDateTime replyTime = null;
            String replyNick = null;
            String replyText = null;
            if (comment.getAnswerTo() != null) {
                replyTime = comment.getAnswerTo().getDateTime();
                replyNick = comment.getAnswerTo().getUser().getNickName();
                replyText = comment.getAnswerTo().getCommentText();
            }
            commentDto.setPositionInTopic(comment.getPositionInTopic());
            commentDto.setTopicId(comment.getTopic().getId());
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
            LOG.debug("Comment dto assembled");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return commentDto;
    }

    @Override
    public List<Comment> getAllComments() {
        LOG.debug("Getting all comments");
        List<Comment> comments = null;
        try {
            comments = commentRepository.findAll();
            LOG.debug("Returned list of {} comments", comments.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return comments;
    }


    private String timeSinceRegistration(LocalDateTime regDate) {
        Duration dur = Duration.between(regDate, LocalDateTime.now());
        long days = dur.toDays();
        return days < 365 ? "С нами уже " + days + " день(-ей)" :
                "С нами больше " + ChronoUnit.YEARS.between(LocalDateTime.now(), LocalDateTime.now().plus(dur)) + " лет";
    }


    @Override
    public List<Comment> getAllCommentsByTopicId(Long id) {
        LOG.debug("Getting all comments for topic with id = {}", id);
        List<Comment> comments = null;
        try {
            comments = commentRepository.findByTopicId(id);
            LOG.debug("Returned list of {} comments", comments.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return comments;
    }

    @Override
    public Comment getCommentById(Long id) {
        LOG.debug("Getting comment with id = {}", id);
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElseThrow(() -> new RuntimeException("not found comment by id: " + id));
    }
}