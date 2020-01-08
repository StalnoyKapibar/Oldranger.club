package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.service.article.ArticleService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll(Sort.by("date").descending());
    }

    @Override
    public Page<Article> getAllByTag(Set<ArticleTag> tagId, Pageable pageable) {
        return articleRepository.findDistinctByArticleTagsIn(tagId, pageable);
    }

    @Override
    public Article getArticleById(long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Page<Article> getArticlesForAnon(Pageable pageable) {
        return articleRepository.getArticlesForAnon(pageable);
    }

    @Override
    public void addArticle(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteArticles(List<Long> ids) {
        articleRepository.deleteAllByIdIn(ids);
    }
}