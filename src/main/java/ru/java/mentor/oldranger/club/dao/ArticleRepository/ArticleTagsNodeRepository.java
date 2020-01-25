package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeRepository extends JpaRepository<ArticleTagsNode, Long> {
    Set<ArticleTagsNode> findByIdIn(List<Long> id);
}
