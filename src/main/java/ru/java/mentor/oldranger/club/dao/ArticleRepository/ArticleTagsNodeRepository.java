package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeRepository extends JpaRepository<ArticleTagsNode, Long> {

    @Query(nativeQuery = true,
            value = "WITH RECURSIVE CTE AS " +
                    "                   ( " +
                    "                       SELECT tag_id, " +
                    "                              parent, " +
                    "                              position, " +
                    "                              article_tags_tree.id, " +
                    "                              1                                AS depth, " +
                    "                              CONCAT(position, '=', tag_id)    AS path, " +
                    "                              CASE " +
                    "                                  WHEN parent not in (select id from article_tags_tree) " +
                    "                                      THEN CONCAT('parent or s " +
                    "one of the progenitors  with id=', parent, ' doesn''t exist') " +
                    "                                  ELSE CONCAT(tag_id) END AS tags_hierarchy " +
                    "                       FROM article_tags_tree " +
                    "                       WHERE parent IS NULL " +
                    "                          or parent not in (select id from article_tags_tree) " +
                    "                       UNION ALL " +
                    "                       SELECT c.tag_id, " +
                    "                              c.parent, " +
                    "                              c.position, " +
                    "                              c.id, " +
                    "                              sc.depth + 1, " +
                    "                              CONCAT(sc.path, ' > ', c.position, '=', c.tag_id), " +
                    "                              CONCAT(sc.tags_hierarchy, ',', c.tag_id) AS tags_hierarchy " +
                    "                       FROM CTE AS sc " +
                    "                                JOIN article_tags_tree AS c ON sc.id = c.parent " +
                    "                                 " +
                    "                   ) " +
                    "SELECT * FROM CTE left join tags as t on CTE.tag_id = t.id order by path")

    List<ArticleTagsNode> findAllChildrenTree();

    @Query(nativeQuery = true,
    value = "WITH RECURSIVE CTE AS   " +
            "                   (   " +
            "                       SELECT parent,   " +
            "                              article_tags_tree.id   " +
            "                       FROM article_tags_tree   " +
            "                       WHERE parent = ?1   " +
            "                       UNION ALL   " +
            "                       SELECT c.parent,   " +
            "                              c.id   " +
            "                       FROM CTE AS sc   " +
            "                                JOIN article_tags_tree AS c ON sc.id = c.parent   " +
            "                   )   " +
            "SELECT id   " +
            "FROM CTE   " +
            "union all   " +
            "select ?1")
    List<Long> findDescendantsAndParentIdsByParentId(Long id);

    void deleteByIdIn(List<Long> ids);


}
