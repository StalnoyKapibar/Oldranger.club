package ru.java.mentor.oldranger.club.service.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.article.Article;

import java.util.List;

public interface ArticleService {

    List<Article> getAllArticles();

    Page<Article> getAllByTag(long tagId, Pageable pageable);

    void addArticle(Article article);

    void deleteArticle(Long id);

    void deleteArticles(List<Long> ids);

    Article getArticleById(long id);

    Page<Article> getArticlesForAnon(Pageable pageable);
}