package ru.java.mentor.oldranger.club.service.forum.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.java.mentor.oldranger.club.dao.ForumRepository.CommentRepository;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.LocalDateTime;


@RunWith(SpringRunner.class)
@SpringBootTest
class CommentServiceImplTest {
    @Autowired
    CommentServiceImpl commentServiceImpl;

    @MockBean
    private TopicService topicService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private UserStatisticService userStatisticService;

    @Test
    public void createComment() {
        User user = new User("String firstName", "String lastName", "String email"
                , "String nickName", new Role("user"));
        Topic topic = new Topic();
        topic.setMessageCount(1L);
        Comment comment = new Comment(topic, user, null, LocalDateTime.now(), "String commentText");
        commentServiceImpl.createComment(comment);
        Mockito.verify(topicService, Mockito.times(1)).editTopicByName(topic);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
        Mockito.verify(userStatisticService, Mockito.times(1)).getUserStaticByUser(comment.getUser());
        Assert.assertEquals(2L, topic.getMessageCount());
    }

    @Test
    void getPageableCommentDtoByTopic() {

    }

    @Test
    void getPageableCommentDtoByUser() {
    }

    @Test
    public void assembleCommentDto() {
        User user = new User("String firstName", "String lastName", "String email"
                , "String nickName", new Role("user"));
        Topic topic = new Topic();
        Comment answerTo = new Comment(topic, user, null, LocalDateTime.now(), "String commentText");
        answerTo.setDateTime(LocalDateTime.now());
        Comment comment = new Comment(topic, user, answerTo, LocalDateTime.now(), "String commentText");
        CommentDto commentDto = commentServiceImpl.assembleCommentDto(comment, user);
        Assert.assertEquals(comment.getId(), commentDto.getCommentId());
        Assert.assertEquals(comment.getPosition(), commentDto.getPositionInTopic());
        Assert.assertEquals(comment.getTopic().getId(), commentDto.getTopicId());
        Assert.assertEquals(comment.getUser(), commentDto.getAuthor());
        Assert.assertEquals(comment.getDateTime(), commentDto.getCommentDateTime());
//        Assert.assertEquals(comment.getAnswerTo().getDateTime(), commentDto.getReplyDateTime());


    }

    @Test
    void getAllComments() {
    }

    @Test
    void getAllCommentsByTopicId() {
    }

    @Test
    void getCommentById() {
    }

    @Test
    void updatePostion() {
    }
}