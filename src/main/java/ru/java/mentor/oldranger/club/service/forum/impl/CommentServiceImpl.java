package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.CommentRepository;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.ImageCommnetService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private UserStatisticService userStatisticService;
    private TopicService topicService;
    private PhotoService photoService;
    private ImageCommnetService imageCommnetService;

    @Override
    public void createComment(Comment comment) {
        log.info("Saving comment {}", comment);
        try {
            Topic topic = comment.getTopic();
            topic.setLastMessageTime(comment.getDateTime());
            long messages = topic.getMessageCount();
            comment.setPosition(++messages);
            topic.setMessageCount(messages);
            topicService.editTopicByName(topic);
            commentRepository.save(comment);
            UserStatistic userStatistic = userStatisticService.getUserStaticByUser(comment.getUser());
            messages = userStatistic.getMessageCount();
            userStatistic.setMessageCount(++messages);
            userStatistic.setLastComment(comment.getDateTime());
            userStatisticService.saveUserStatic(userStatistic);
            log.info("Comment saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteComment(Long id) {
        log.info("Deleting comment with id = {}", id);
        try {
            commentRepository.deleteById(id);
            log.info("Comment {} deleted", id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateComment(Comment comment) {
        log.info("Updating comment with id = {}", comment.getId());
        try {
            comment.setUpdateTime(LocalDateTime.now());
            commentRepository.save(comment);
            log.info("Comment {} updated", comment.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Page<Comment> getPageableCommentByTopic(Topic topic, Pageable pageable) {
        log.debug("Getting page {} of comments for topic with id = {}", pageable.getPageNumber(), topic.getId());
        Page<Comment> page = null;
        try {
            page = commentRepository.findByTopic(topic, pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    public Page<CommentDto> getPageableCommentDtoByTopic(Topic topic, Pageable pageable, int position, User user) {
        log.debug("Getting page {} of comment dtos for topic with id = {}", pageable.getPageNumber(), topic.getId());
        Page<CommentDto> page = null;
        List<Comment> list = new ArrayList<>();
        try {
            commentRepository.findByTopic(topic,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() + position, pageable.getSort()))
                    .map(list::add);
            List<CommentDto> dtoList = null;
            if (list.size() != 0) {
                dtoList = list.subList(
                        Math.min(position, list.size() - 1),
                        Math.min(position + pageable.getPageSize(), list.size()))
                        .stream().map(a -> assembleCommentDto(a, user)).collect(Collectors.toList());
            } else {
                dtoList = Collections.emptyList();
            }
            page = new PageImpl<CommentDto>(dtoList, pageable, dtoList.size());
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public Page<CommentDto> getPageableCommentDtoByUser(User user, Pageable pageable) {
        log.debug("Getting page {} of comment dtos for user with id = {}", pageable.getPageNumber(), user.getId());
        Page<CommentDto> page = null;
        try {
            page = commentRepository.findByUser(user, pageable).map(a -> assembleCommentDto(a, user));
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }


    public CommentDto assembleCommentDto(Comment comment, User user) {
        log.debug("Assembling comment {} dto", comment);
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
            commentDto.setCommentId(comment.getId());
            commentDto.setPositionInTopic(comment.getPosition());
            commentDto.setTopicId(comment.getTopic().getId());
            commentDto.setTopicName(comment.getTopic().getName());
            commentDto.setAuthor(comment.getUser());
            commentDto.setCommentDateTime(comment.getDateTime());
            commentDto.setCommentUpdateTime(comment.getUpdateTime());
            commentDto.setMessageCount(userStatisticService.getUserStaticById(comment.getUser().getId()).getMessageCount());
            commentDto.setReplyDateTime(replyTime);
            commentDto.setReplyNick(replyNick);
            commentDto.setReplyText(replyText);
            commentDto.setCommentText(comment.getCommentText());
            commentDto.setPhotos(photoService.findByAlbumTitleAndDescription("PhotoAlbum by " +
                    comment.getTopic().getName(), comment.getId().toString()));
            commentDto.setDeleted(comment.isDeleted());

            boolean allowedEditingTime = LocalDateTime.now().compareTo(comment.getDateTime().plusDays(7)) >= 0;
            if (user == null) {
                commentDto.setUpdatable(false);
            } else if (user.getId().equals(comment.getUser().getId()) && !allowedEditingTime) {
                commentDto.setUpdatable(true);
            } else {
                commentDto.setUpdatable(false);
            }
            log.debug("Comment dto assembled");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return commentDto;
    }

    @Override
    public List<Comment> getAllComments() {
        log.debug("Getting all comments");
        List<Comment> comments = null;
        try {
            comments = commentRepository.findAll();
            log.debug("Returned list of {} comments", comments.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        log.debug("Getting all comments for topic with id = {}", id);
        List<Comment> comments = null;
        try {
            comments = commentRepository.findByTopicId(id);
            log.debug("Returned list of {} comments", comments.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return comments;
    }

    @Override
    public Comment getCommentById(Long id) {
        log.debug("Getting comment with id = {}", id);
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElseThrow(() -> new RuntimeException("not found comment by id: " + id));
    }

    @Override
    public void updatePostion(Long topicID, Long deletedPosition) {
        log.debug("Updating comments position with topic_id = {}", topicID);
        List<Comment> comments = commentRepository.findByPositionGreaterThanAndTopicId(deletedPosition, topicID);
        comments.forEach(a -> {
            a.setPosition(a.getPosition() - 1);
            commentRepository.save(a);
        });
    }

    @Override
    public boolean isEmptyComment(String comment) {
        return StringUtils.isBlank(comment.replaceAll("<.+?>", ""));
    }

    public List<Comment> getChildComment(Comment comment) {
        log.debug("Getting list childComment with idAnswerTo = {}", comment.getId());
        List<Comment> childComment = commentRepository.findAllByAnswerTo(comment);
        return childComment;
    }
}