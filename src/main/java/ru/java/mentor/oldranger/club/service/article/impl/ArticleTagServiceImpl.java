package ru.java.mentor.oldranger.club.service.article.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    private ArticleTagRepository articleTagRepository;

    @Autowired
    public ArticleTagServiceImpl(ArticleTagRepository articleTagRepository) {
        this.articleTagRepository = articleTagRepository;
    }

    @Override
    public List<ArticleTag> getAllTags() {
        return articleTagRepository.findAll();
    }

    @Override
    public ArticleTag getTagById(Long id) {
        return articleTagRepository.findById(id).orElse(null);
    }

    @Override
    public void addTag(ArticleTag articleTag) {
        articleTagRepository.save(articleTag);
    }

    @Override
    public void updateArticleTag(ArticleTag articleTag) {
        articleTagRepository.save(articleTag);
    }

    @Override
    public void deleteArticleTag(ArticleTag articleTag) {
        articleTagRepository.delete(articleTag);
    }

    @Override
    public Set<ArticleTag> addTagsToSet(List<Long> tagsId) {
        Set<ArticleTag> articleTags = new HashSet<>();
        for (Long tagId : tagsId) {
            articleTagRepository.findById(tagId).ifPresent(articleTags::add);
        }
        return articleTags;
    }
}
