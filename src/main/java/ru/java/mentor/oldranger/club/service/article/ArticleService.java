package ru.java.mentor.oldranger.club.service.article;

import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleComment;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {

    List<Article> getAllArticles();

    List<Article> getAllByTag(long tagId);

    void addArticle(Article article);

    Article getArticleById(long id);

    void addCommentToArticle(ArticleComment articleComment);

    ArticleCommentDto conversionCommentToDto(ArticleComment articleComment);

    ArticleComment getCommentById(Long id);

    void updateArticleComment(ArticleComment articleComment);

    void deleteComment(Long id);
}