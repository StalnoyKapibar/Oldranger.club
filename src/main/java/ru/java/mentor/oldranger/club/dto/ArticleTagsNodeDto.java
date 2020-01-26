package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

@Data
@Schema(description = "This is Tre of Tags (ArticleTagsNode tree -  every leaf points to ArticleTag) ",
        requiredProperties = {"idUser", "idTopic"})
public class ArticleTagsNodeDto {
    private Long id;
    private ArticleTagsNode parent;
    private Integer position;
    private ArticleTag tag;
}
