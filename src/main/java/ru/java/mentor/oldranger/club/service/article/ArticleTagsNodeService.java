package ru.java.mentor.oldranger.club.service.article;

import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeService {

    List<ArticleTagsNode> getAllTagsNodes();

    List<ArticleTagsNode> getAllTagsNodesTree();

    ArticleTagsNode getTagsNodeById(Long id);

    void addTagsNode(ArticleTagsNode tagsNode);

    void updateArticleTagsNode(ArticleTagsNode tagsNode);

    void deleteArticleTagsNode(ArticleTagsNode tagsNode);

    Set<ArticleTagsNode> addArticleTagsNodeToSet(List<Long> tagsNodes);
}
