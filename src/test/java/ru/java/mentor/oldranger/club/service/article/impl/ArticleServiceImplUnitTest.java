package ru.java.mentor.oldranger.club.service.article.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleCommentRepository;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleComment;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ArticleServiceImplUnitTest {

    private ArticleRepository articleRepository;
    private ArticleCommentRepository articleCommentRepository;
    private UserStatisticService userStatisticService;
    private ArticleService articleService;

    @BeforeEach
    public void setup() {
        this.articleRepository = Mockito.mock(ArticleRepository.class);
        this.articleCommentRepository = Mockito.mock(ArticleCommentRepository.class);
        this.userStatisticService = Mockito.mock(UserStatisticService.class);
        this.articleService = new ArticleServiceImpl(
                articleRepository, articleCommentRepository, userStatisticService);
    }

    @Test
    public void getCommentById() {
        ArticleComment articleComment = new ArticleComment();
        when(this.articleCommentRepository.findById(any())).thenReturn(Optional.of(articleComment));

        ArticleComment commentById = articleService.getCommentById(10l);
        assertEquals(articleComment, commentById);
    }
}