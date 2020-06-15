package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import javax.persistence.Tuple;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ArticleTagsNodeRepository extends JpaRepository<ArticleTagsNode, Long> {

    @Query(nativeQuery = true,
            value = "WITH RECURSIVE CTE AS (\n" +
                    "    SELECT tag_id,\n" +
                    "           parent,\n" +
                    "           position,\n" +
                    "           article_tags_tree.id,\n" +
                    "           1              AS depth,\n" +
                    "           CONCAT(tag_id) AS tags_hierarchy\n" +
                    "    FROM article_tags_tree\n" +
                    "    WHERE parent IS NULL\n" +
                    "       or parent not in (select id from article_tags_tree)\n" +
                    "    UNION ALL\n" +
                    "    SELECT c.tag_id,\n" +
                    "           c.parent,\n" +
                    "           c.position,\n" +
                    "           c.id,\n" +
                    "           sc.depth + 1,\n" +
                    "           CONCAT(sc.tags_hierarchy, ',', c.tag_id) AS tags_hierarchy\n" +
                    "    FROM CTE AS sc\n" +
                    "             JOIN article_tags_tree AS c ON sc.id = c.parent\n" +
                    ")\n" +
                    "SELECT tag_id,\n" +
                    "       parent,\n" +
                    "       position,\n" +
                    "       id,\n" +
                    "       depth,\n" +
                    "       tags_hierarchy,\n" +
                    "       CASE\n" +
                    "           WHEN parent not in (select id from article_tags_tree)\n" +
                    "               THEN CONCAT(tag_name, ' - for this tag - some of the ancestors with id=', parent,\n" +
                    "                           ' doesn''t exist')\n" +
                    "           ELSE tag_name END as tag_name,\n" +
                    "       t_tag_id\n" +
                    "FROM CTE\n" +
                    "         left join (select tag_name, id as t_tag_id from tags) as t on CTE.tag_id = t.t_tag_id\n" +
                    "order by tags_hierarchy;")
    List<Tuple> findAllChildrenTree();

    default List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes() {
        return findAllChildrenTree().stream()
                .map(e -> new ArticleTagsNodeDto(
                        Long.valueOf(e.get("id").toString()),
                        e.get("parent") == null ? null : Long.valueOf(e.get("parent").toString()),
                        Integer.valueOf(e.get("position").toString()),
                        e.get("tag_name", String.class),
                        Arrays.stream(e.get("tags_hierarchy", String.class).split(",")).mapToInt(Integer::parseInt).toArray())).collect(Collectors.toList());
    }

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

    Set<ArticleTagsNode> findByIdIn(List<Long> id);
}
