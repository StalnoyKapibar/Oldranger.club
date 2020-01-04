package ru.java.mentor.oldranger.club.service.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

import java.util.List;

public interface ArticleService {

    List<Article> getAllArticles();

    List<Article> getAllByTag(long tagId);

    void addArticle(Article article);

    Article getArticleById(long id);

    Page<Article> getArticlesForAnon(boolean isHide, Pageable pageable);
}