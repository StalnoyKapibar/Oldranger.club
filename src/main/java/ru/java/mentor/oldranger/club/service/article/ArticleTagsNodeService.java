package ru.java.mentor.oldranger.club.service.article;

import org.springframework.http.ResponseEntity;
import ru.java.mentor.oldranger.club.dto.ArticleAndCommentsDto;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeService {

    List<ArticleTagsNode> findAll();

    List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes();

    ArticleTagsNode findById(Long id);

    void save(ArticleTagsNode tagsNode);

    void delete(ArticleTagsNode tagsNode);

    void deleteById(Long id);

    Set<ArticleTagsNode> findByIdIn(List<Long> tagsNodes);
}
