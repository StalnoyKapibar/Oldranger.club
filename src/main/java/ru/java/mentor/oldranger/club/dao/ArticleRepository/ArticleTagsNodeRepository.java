package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import java.util.List;
import java.util.Set;

public interface ArticleTagsNodeRepository extends JpaRepository<ArticleTagsNode, Long> {
    Set<ArticleTagsNode> findByIdIn(List<Long> id);

    @Query(nativeQuery = true,
            value = "WITH RECURSIVE CTE AS " +
                    "                   ( " +
                    "                       SELECT tag_id , CONCAT('', tag_id) as tags_hierarchy , CONCAT('', COALESCE(parent,'null')) as tags_node_hierarchy ,parent, position,  id,   1 AS depth, name, CONCAT(position,'=',name) AS path, position as pos " +
                    "                       FROM Old.article_tags_tree " +
                    "                       WHERE parent IS NULL " +
                    "                       UNION ALL " +
                    "                       SELECT  c.tag_id, CONCAT(sc.tags_hierarchy,'=', c.tag_id),  CONCAT(sc.tags_node_hierarchy,'=', COALESCE(c.parent,'')), c.parent,  c. position,c.id,  sc.depth + 1, c.name,  CONCAT( sc.path, ' > ', c.position,'=' , c.name), sc.pos " +
                    "                       FROM CTE AS sc " +
                    "                                JOIN Old.article_tags_tree AS c ON sc.id = c.parent " +
                    "                   ) " +
                    "SELECT *FROM CTE left join Old.tags as t on CTE.tag_id = t.id order by path;")
    List<ArticleTagsNode>  getAllTagsNodesTree();
}
