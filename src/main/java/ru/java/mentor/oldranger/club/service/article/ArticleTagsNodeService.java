package ru.java.mentor.oldranger.club.service.article;

import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeService {

    List<ArticleTagsNode> findAll();

    List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes();

    ArticleTagsNode findById(Long id);

    ArticleTagsNode save(ArticleTagsNode tagsNode);

    void deleteById(Long id);

    ArticleTagsNode saveAll(List<ArticleTagsNode> tagsNode);

    Set<ArticleTag> findArticleTagSetByListTagsNodeId(List<Long> tagsNodeId);
 }
