package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

@Data
@AllArgsConstructor
@Schema(description = "parantId -указывает на id вышестоящего родителя, таким образом об, " +
        "если parantId -1 (минус единица) означает верхний уровнь, " +
        "Порядок вывода элементов  соответствует обходу дерева по веткам , а иерархия (уровень)" +
        " обеспечивается взаимосвязью реквизитов id и parentId." +
        "tagsHierarchy следует вернуть на БЭКЕНД - в случае выбора конкретного тега на дереве. ",
        requiredProperties = { "tag", "tagsHierarchy"})
public class ArticleTagsNodeDto {
    private Long id;
    private Long parentId;
    private String  tag;
    private String tagsHierarchy;

    public ArticleTagsNodeDto(Long id, Long parentId,  Object tag, String tagsHierarchy) {
        this.id = id;
        this.parentId = parentId;
        this.tag = tag == null ? "NULL" : tag.toString();
        this.tagsHierarchy = tagsHierarchy;
    }
}
