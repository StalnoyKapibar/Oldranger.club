package ru.java.mentor.oldranger.club.service.article;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dto.ArticleAndCommentsDto;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.dto.ArticleListAndCountArticlesDto;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;
import java.util.Set;

public interface ArticleService {

    Page<Article> getAllArticles(Pageable pageable);

    Page<Article> getAllByTag(Set<ArticleTag> tagId, Pageable pageable);

    Page<Article> getAllByTitle(String title, Pageable pageable);

    Page<Article> getArticleDraftByUser(User user, Pageable pageable);

    Article addArticle(Article article);

    void deleteArticle(Long id);

    void deleteArticles(List<Long> ids);

    Article getArticleById(Long id);

    void addCommentToArticle(ArticleComment articleComment);

    ArticleCommentDto assembleCommentToDto(ArticleComment articleComment);

    ArticleComment getCommentById(Long id);

    ArticleComment updateArticleComment(ArticleComment articleComment);

    void deleteComment(Long id);

    List<ArticleCommentDto> getAllByArticle(Article article);

    Page<Article> getArticlesForAnon(Pageable pageable);

    ArticleListAndCountArticlesDto assembleArticleListAndCountArticleDto(List<Article> articles, long countArticle);

}