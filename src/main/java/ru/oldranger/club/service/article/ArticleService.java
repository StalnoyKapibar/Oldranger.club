package ru.oldranger.club.service.article;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oldranger.club.dto.ArticleCommentDto;
import ru.oldranger.club.model.article.Article;
import ru.oldranger.club.model.article.ArticleTag;
import ru.oldranger.club.model.comment.ArticleComment;
import ru.oldranger.club.model.user.User;

import java.util.List;
import java.util.Set;

public interface ArticleService {

    Page<Article> getAllArticles(Pageable pageable);

    Page<Article> getAllByTag(Set<ArticleTag> tagId, Pageable pageable);

    Page<Article> getArticleDraftByUser(User user, Pageable pageable);

    void addArticle(Article article);

    void deleteArticle(Long id);

    void deleteArticles(List<Long> ids);

    Article getArticleById(Long id);

    void addCommentToArticle(ArticleComment articleComment);

    ArticleCommentDto assembleCommentToDto(ArticleComment articleComment);

    ArticleComment getCommentById(Long id);

    void updateArticleComment(ArticleComment articleComment);

    void deleteComment(Long id);

    Page<ArticleCommentDto> getAllByArticle(Article article, Pageable pageable);

    Page<Article> getArticlesForAnon(Pageable pageable);
}