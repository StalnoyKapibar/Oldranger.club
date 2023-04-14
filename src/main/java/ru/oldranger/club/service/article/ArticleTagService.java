package ru.oldranger.club.service.article;

import ru.oldranger.club.model.article.ArticleTag;

import java.util.List;
import java.util.Set;

public interface ArticleTagService {

    List<ArticleTag> getAllTags();

    ArticleTag getTagById(Long id);

    void addTag(ArticleTag articleTag);

    void updateArticleTag(ArticleTag articleTag);

    void deleteArticleTag(ArticleTag articleTag);

    Set<ArticleTag> addTagsToSet (List<Long> tags);
}
