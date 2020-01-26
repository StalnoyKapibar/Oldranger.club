package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

@Data
@AllArgsConstructor
@Schema(description = "This is Tre of Tags (ArticleTagsNode tree -  every leaf points to ArticleTag) ",
        requiredProperties = {"id", "tag"})
public class ArticleTagsNodeDto {
    private Long id;
    private Long parentId;
//    private ArticleTagsNode parent;
    private Integer position;
    private ArticleTag tag;
}
