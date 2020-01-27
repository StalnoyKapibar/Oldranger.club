package ru.java.mentor.oldranger.club.service.article;

import org.springframework.http.ResponseEntity;
import ru.java.mentor.oldranger.club.dto.ArticleAndCommentsDto;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeService {

    List<ArticleTagsNode> getAllTagsNodes();

    List<ArticleTagsNodeDto> getAllTagsNodesTree();

    ArticleTagsNode getTagsNodeById(Long id);

    void addTagsNode(ArticleTagsNode tagsNode);

    void updateArticleTagsNode(ArticleTagsNode tagsNode);

//    void deleteArticleTagsNode(ArticleTagsNode tagsNode);

    Set<ArticleTagsNode> addArticleTagsNodeToSet(List<Long> tagsNodes);
}
