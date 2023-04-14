package ru.oldranger.club.service.article;

import ru.oldranger.club.dto.ArticleTagsNodeDto;
import ru.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;

public interface ArticleTagsNodeService {

    List<ArticleTagsNode> findAll();

    List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes();

    ArticleTagsNode findById(Long id);

    void save(ArticleTagsNode tagsNode);

    void deleteById(Long id);

 }
