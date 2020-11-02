package ru.java.mentor.oldranger.club.service.article.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleCommentRepository;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
class ArticleServiceImplTest {
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleRepository articleRepository = Mockito.mock(ArticleRepository.class);

    @Mock
    private ArticleCommentRepository articleCommentRepository = Mockito.mock(ArticleCommentRepository.class);

    @Mock
    private UserStatisticService userStatisticService = Mockito.mock(UserStatisticService.class);

    @Mock
    private Pageable pageable = Mockito.mock(Pageable.class);

    @Mock
    private PhotoService photoService = Mockito.mock(PhotoService.class);
    @BeforeEach
    void initSomeCase() {
        articleService = new ArticleServiceImpl(articleRepository, articleCommentRepository, userStatisticService, photoService);
    }

    @Test
    public void addCommentToArticle() {
        User user = new User();
        UserStatistic userStatistic = new UserStatistic(user);
        userStatistic.setMessageCount(1L);
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text",true, true);
        article.setCommentCount(1L);
        ArticleComment articleComment = new ArticleComment(article, user, null, LocalDateTime.now(), "comment text");
        Mockito.when(userStatisticService.getUserStaticByUser(articleComment.getUser())).thenReturn(userStatistic);
        articleService.addCommentToArticle(articleComment);
        Mockito.verify(articleCommentRepository, Mockito.times(1)).save(articleComment);
        Assert.assertEquals(2L, article.getCommentCount());
        Assert.assertEquals(2L, userStatistic.getMessageCount());
    }

    @Test
    public void conversionCommentToDto() {
        User user = new User();
        user.setNickName("NickName");
        UserStatistic userStatistic = new UserStatistic(user);
        userStatistic.setMessageCount(1L);
        ArticleComment answerTo = new ArticleComment(null, user, null, LocalDateTime.now(), "comment text");
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text", true, true);
        ArticleComment articleComment = new ArticleComment(article, user, answerTo, LocalDateTime.now(), "comment text");
        articleComment.setPosition(3L);
        article.setId(3L);
        Mockito.when(userStatisticService.getUserStaticById(articleComment.getUser().getId())).thenReturn(userStatistic);
        ArticleCommentDto articleCommentDto = articleService.assembleCommentToDto(articleComment, user);
        Assert.assertEquals(articleComment.getUser().getNickName(), articleCommentDto.getReplyNick());
        Assert.assertEquals(articleComment.getCommentText(), articleCommentDto.getReplyText());
        Assert.assertEquals(articleComment.getDateTime(), articleCommentDto.getCommentDateTime());
        Assert.assertEquals(articleComment.getArticle().getId(), articleCommentDto.getArticleId());
        Assert.assertEquals(articleComment.getPosition(), articleCommentDto.getPosition());
    }

    @Test
    public void getAllByArticle() {
        User user = new User();
        user.setNickName("NickName");
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text", true, true);
        article.setId(1L);
        ArticleCommentDto articleCommentDto = new ArticleCommentDto();
        articleCommentDto.setArticleId(article.getId());
        Mockito.when(pageable.getPageNumber()).thenReturn(1);
        articleService.getAllByArticle(article, article.getUser());
        Mockito.verify(articleCommentRepository, Mockito.times(1))
                .findByArticle(article);
    }
}