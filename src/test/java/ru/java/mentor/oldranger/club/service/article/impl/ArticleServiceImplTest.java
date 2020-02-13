package ru.java.mentor.oldranger.club.service.article.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
    private Article article;

    @Mock
    private ArticleComment articleComm;

    @Mock
    private User user;

    @Mock
    Pageable pageable;

    @Test
    void addCommentToArticle() {
        ArticleComment articleComment = new ArticleComment(article, user, articleComm
                , LocalDateTime.now(), "comment text");
        Mockito.when(userStatistic.getMessageCount()).thenReturn(1L);
        Mockito.when(userStatisticService.getUserStaticByUser(articleComment.getUser())).thenReturn(userStatistic);
        articleService.addCommentToArticle(articleComment);

        Mockito.verify(articleCommentRepository, Mockito.times(1)).save(articleComment);

    }

    @Test
    void conversionCommentToDto() {
        ArticleComment articleComment = new ArticleComment(article, user, articleComm
                , LocalDateTime.now(), "comment text");
        Mockito.when(articleComment.getAnswerTo().getDateTime()).thenReturn(LocalDateTime.now());
        Mockito.when(articleComment.getArticle().getId()).thenReturn(1L);
        Mockito.when(articleComment.getAnswerTo().getUser()).thenReturn(user);
        Mockito.when(user.getNickName()).thenReturn("NickName");
        Mockito.when(articleComment.getAnswerTo().getCommentText()).thenReturn("Text");

        articleComment.setPosition(3L);
        article.setId(3L);

        Mockito.when(userStatisticService.getUserStaticById(ArgumentMatchers.anyLong())).thenReturn(userStatistic);
        Mockito.when(userStatistic.getMessageCount()).thenReturn(1L);

        ArticleCommentDto articleCommentDto = articleService.conversionCommentToDto(articleComment);

        Assert.assertEquals("NickName", articleCommentDto.getReplyNick());
        Assert.assertEquals("Text", articleCommentDto.getReplyText());
        Assert.assertEquals(articleComment.getDateTime(), articleCommentDto.getCommentDateTime());
    }

    @Test
    void getAllByArticle() {
        ArticleCommentDto articleCommentDto = new ArticleCommentDto();
        articleCommentDto.setArticleId(article.getId());
        Mockito.when(pageable.getPageNumber()).thenReturn(1);

        Page<ArticleCommentDto> list = articleService.getAllByArticle(article, pageable);
        Mockito.verify(articleCommentRepository, Mockito.times(1))
                .findByArticle(article, pageable);
    }
}