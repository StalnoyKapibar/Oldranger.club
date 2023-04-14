package ru.oldranger.club.service.article.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import ru.oldranger.club.dao.ArticleRepository.ArticleCommentRepository;
import ru.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.oldranger.club.dto.ArticleCommentDto;
import ru.oldranger.club.model.article.Article;
import ru.oldranger.club.model.comment.ArticleComment;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserStatistic;
import ru.oldranger.club.service.user.UserStatisticService;

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

    @BeforeEach
    void initSomeCase() {
        articleService = new ArticleServiceImpl(articleRepository, articleCommentRepository, userStatisticService);
    }

    @Test
    public void addCommentToArticle() {
        User user = new User();
        UserStatistic userStatistic = new UserStatistic(user);
        userStatistic.setMessageCount(1L);
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text", true);
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
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text", true);
        ArticleComment articleComment = new ArticleComment(article, user, answerTo, LocalDateTime.now(), "comment text");
        articleComment.setPosition(3L);
        article.setId(3L);
        Mockito.when(userStatisticService.getUserStaticById(articleComment.getUser().getId())).thenReturn(userStatistic);
        ArticleCommentDto articleCommentDto = articleService.assembleCommentToDto(articleComment);
        Assert.assertEquals(articleComment.getUser().getNickName(), articleCommentDto.getReplyNick());
        Assert.assertEquals(articleComment.getCommentText(), articleCommentDto.getReplyText());
        Assert.assertEquals(articleComment.getDateTime(), articleCommentDto.getCommentDateTime());
        Assert.assertEquals(articleComment.getArticle().getId(), articleCommentDto.getArticleId());
        Assert.assertEquals(articleComment.getPosition(), articleCommentDto.getPositionInArticle());
    }

    @Test
    public void getAllByArticle() {
        Article article = new Article("String title", null, null, LocalDateTime.now(), "String text", true);
        article.setId(1L);
        ArticleCommentDto articleCommentDto = new ArticleCommentDto();
        articleCommentDto.setArticleId(article.getId());
        Mockito.when(pageable.getPageNumber()).thenReturn(1);
        articleService.getAllByArticle(article, pageable);
        Mockito.verify(articleCommentRepository, Mockito.times(1))
                .findByArticle(article, pageable);
    }
}