package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagRepository;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ArticleTagServiceImpl implements ArticleTagService {

    private ArticleTagRepository articleTagRepository;
    private ArticleTagsNodeRepository tagsNodeRepository;

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
        Set<ArticleTagsNode> idIn = tagsNodeRepository.findByIdIn(tagsId);
        Set<ArticleTag> byIdIn = new HashSet<>();
        for (ArticleTagsNode node : idIn) {
            byIdIn.add(new ArticleTag(node.getId(), node.getTag().getName()));
        }
        return byIdIn;
    }

    @Override
    public ArticleTag getTagByTagName(String tagName) {
        return articleTagRepository.findArticleTagByName(tagName);
    }
}
