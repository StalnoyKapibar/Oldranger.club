package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.service.article.ArticleService;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll(Sort.by("date").descending());
    }

    @Override
    public List<Article> getAllByTag(long tagId) {
        return articleRepository.findAllByArticleTags_id(tagId);
    }

    @Override
    public void addArticle(Article article) {
        articleRepository.save(article);
    }
}