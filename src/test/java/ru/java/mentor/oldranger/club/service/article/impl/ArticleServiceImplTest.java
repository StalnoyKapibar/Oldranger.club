package ru.java.mentor.oldranger.club.service.article.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleCommentRepository;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
class ArticleServiceImplTest {
    @Autowired
    private ArticleServiceImpl articleService;

    @MockBean
    private ArticleCommentRepository articleCommentRepository;

    @MockBean
    private UserStatisticService userStatisticService;

    @MockBean
    private UserStatistic userStatistic;

    @Mock
    private Pageable pageable;

    @Test
    public void addCommentToArticle() {
        User user = new User();
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text", true);
        ArticleComment articleComm = null;
        article.setCommentCount(1L);
        ArticleComment articleComment = new ArticleComment(article, user, articleComm
                , LocalDateTime.now(), "comment text");
        Mockito.when(userStatistic.getMessageCount()).thenReturn(1L);
        Mockito.when(userStatisticService.getUserStaticByUser(articleComment.getUser())).thenReturn(userStatistic);
        articleService.addCommentToArticle(articleComment);

        Mockito.verify(articleCommentRepository, Mockito.times(1)).save(articleComment);
        Assert.assertEquals(2L, article.getCommentCount());

    }

    @Test
    public void conversionCommentToDto() {
        User user = new User();
        user.setNickName("NickName");
        ArticleComment answerTo = new ArticleComment(null, user, null
                , LocalDateTime.now(), "comment text");
        Article article = new Article("String title", user, null, LocalDateTime.now(), "String text", true);
        ArticleComment articleComment = new ArticleComment(article, user, answerTo
                , LocalDateTime.now(), "comment text");

        articleComment.setPosition(3L);
        article.setId(3L);
        Mockito.when(userStatisticService.getUserStaticById(articleComment.getUser().getId())).thenReturn(userStatistic);
        Mockito.when(userStatistic.getMessageCount()).thenReturn(2L);

        ArticleCommentDto articleCommentDto = articleService.conversionCommentToDto(articleComment);

        Assert.assertEquals(articleComment.getUser().getNickName(), articleCommentDto.getReplyNick());
        Assert.assertEquals(articleComment.getCommentText(), articleCommentDto.getReplyText());
        Assert.assertEquals(articleComment.getDateTime(), articleCommentDto.getCommentDateTime());

    }

    @Test
    public void getAllByArticle() {
        Article article = new Article("String title", null, null, LocalDateTime.now(), "String text", true);
        article.setId(1L);
        ArticleCommentDto articleCommentDto = new ArticleCommentDto();
        articleCommentDto.setArticleId(article.getId());
        Mockito.when(pageable.getPageNumber()).thenReturn(1);

        Page<ArticleCommentDto> list = articleService.getAllByArticle(article, pageable);
        Mockito.verify(articleCommentRepository, Mockito.times(1))
                .findByArticle(article, pageable);
    }
}