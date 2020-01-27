package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

@Data
@AllArgsConstructor
@Schema(description = "This is Tre of Tags (ArticleTagsNode tree -  every leaf points to ArticleTag) ",
        requiredProperties = { "tag", "tagsHierarchy"})
public class ArticleTagsNodeDto {
    private Long parentId;
    private Integer position;
    private String  tag;
    private String tagsHierarchy;

    public ArticleTagsNodeDto(Long parentId, Integer position, Object tag, String tagsHierarchy) {
        this.parentId = parentId;
        this.position = position;
        this.tag = tag == null ? "NULL" : tag.toString();
        this.tagsHierarchy = tagsHierarchy;
    }
}
