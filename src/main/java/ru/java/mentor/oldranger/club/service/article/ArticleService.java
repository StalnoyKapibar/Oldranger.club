package ru.java.mentor.oldranger.club.service.article;

import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

import java.util.List;

public interface ArticleService {

    List<Article> getAllArticles();

    List<Article> getAllByTag(ArticleTag articleTag);

    List<Article> getAllByTagRecursive(ArticleTag articleTag);

    void addArticle(Article article);
}