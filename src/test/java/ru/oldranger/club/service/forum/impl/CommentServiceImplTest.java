package ru.oldranger.club.service.forum.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.oldranger.club.dao.ForumRepository.CommentRepository;
import ru.oldranger.club.dto.CommentDto;
import ru.oldranger.club.model.comment.Comment;
import ru.oldranger.club.model.forum.Topic;
import ru.oldranger.club.model.user.Role;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserStatistic;
import ru.oldranger.club.service.forum.TopicService;
import ru.oldranger.club.service.media.PhotoService;
import ru.oldranger.club.service.user.UserStatisticService;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
class CommentServiceImplTest {

    private CommentServiceImpl commentServiceImpl;

    @Mock
    private TopicService topicService = Mockito.mock(TopicServiceImpl.class);

    @Mock
    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    @Mock
    private UserStatisticService userStatisticService = Mockito.mock(UserStatisticService.class);

    @Mock
    private ImageCommentServiceImpl imageCommentService = Mockito.mock(ImageCommentServiceImpl.class);

    @Mock
    private PhotoService photoService = Mockito.mock(PhotoService.class);

    @BeforeEach
    void initSomeCase() {
        commentServiceImpl = new CommentServiceImpl(commentRepository, userStatisticService, topicService, photoService, imageCommentService);
    }

    @Test
    public void createComment() {
        User user = new User("String firstName", "String lastName", "String email", "String nickName", new Role("ROLE_ADMIN"));
        Topic topic = new Topic();
        topic.setMessageCount(1L);
        Comment comment = new Comment(topic, user, null, LocalDateTime.now(), "String commentText");
        UserStatistic userStatistic = new UserStatistic(user);
        userStatistic.setMessageCount(2L);
        userStatistic.setId(2L);
        Mockito.when(userStatisticService.getUserStaticByUser(comment.getUser())).thenReturn(userStatistic);
        commentServiceImpl.createComment(comment);
        Mockito.verify(topicService, Mockito.times(1)).editTopicByName(topic);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
        Mockito.verify(userStatisticService, Mockito.times(1)).saveUserStatic(userStatistic);
        Assert.assertEquals(2L, topic.getMessageCount());
        Assert.assertEquals(3L, userStatistic.getMessageCount());
        Assert.assertEquals(userStatistic.getLastComment(), comment.getDateTime());
    }

    @Test
    public void assembleCommentDto() {
        User user = new User("String firstName", "String lastName", "String email", "String nickName", null);
        user.setId(1L);
        Topic topic = new Topic("String name", user, LocalDateTime.now(), null, null, true, false);
        topic.setId(1L);
        Comment answerTo = new Comment(topic, user, null, LocalDateTime.now(), "String commentText");
        answerTo.setDateTime(LocalDateTime.now());
        Comment comment = new Comment(topic, user, answerTo, LocalDateTime.now(), "String commentText");
        comment.setId(1L);
        UserStatistic userStatistic = new UserStatistic();
        userStatistic.setMessageCount(1L);
        Mockito.when(userStatisticService.getUserStaticById(comment.getUser().getId())).thenReturn(userStatistic);
        CommentDto commentDto = commentServiceImpl.assembleCommentDto(comment, user);
        Assert.assertEquals(comment.getId(), commentDto.getCommentId());
        Assert.assertEquals(comment.getPosition(), commentDto.getPositionInTopic());
        Assert.assertEquals(comment.getTopic().getId(), commentDto.getTopicId());
        Assert.assertEquals(comment.getUser(), commentDto.getAuthor());
        Assert.assertEquals(comment.getDateTime(), commentDto.getCommentDateTime());
        Assert.assertEquals(comment.getAnswerTo().getDateTime(), commentDto.getReplyDateTime());
        Assert.assertEquals(comment.getAnswerTo().getUser().getNickName(), commentDto.getReplyNick());
        Assert.assertEquals(comment.getAnswerTo().getCommentText(), commentDto.getReplyText());
        Assert.assertEquals(comment.getCommentText(), commentDto.getCommentText());
        Mockito.verify(photoService, Mockito.times(1))
                .findByAlbumTitleAndDescription(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertTrue(commentDto.isUpdatable());
        user = null;
        commentDto = commentServiceImpl.assembleCommentDto(comment, user);
        Assert.assertFalse(commentDto.isUpdatable());
    }
}