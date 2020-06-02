package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "parentId - указывает на id вышестоящего родителя, таким образом, " +
        "если parentId = -1 (минус единица), то это означает верхний уровень. " +
        "Порядок вывода элементов  соответствует обходу дерева по веткам , а иерархия (уровень)" +
        " обеспечивается взаимосвязью реквизитов id и parentId.  " +
        "На БЭКЕНД следует вернуть tagsHierarchy - в случае выбора конкретного тега на дереве. " +
        "position -  отвечает  за сортировку тэгов в пределах  текущего уровня иерархии",
        requiredProperties = {"tag", "tagsHierarchy"})
public class ArticleTagsNodeDto {
    private Long id;
    private Long parentId;
    private Integer position;
    private String tag;
    private int[] tagsHierarchy;
}
