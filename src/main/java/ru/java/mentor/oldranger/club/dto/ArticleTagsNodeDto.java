package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

@Data
@AllArgsConstructor
@Schema(description = "parantId -указывает на id вышестоящего родителя, таким образом об, " +
        "если parantId -1 (минус единица) означает верхний уровнь, " +
        "Порядок вывода элементов  соответствует обходу дерева по веткам , а иерархия (уровень)" +
        " обеспечивается взаимосвязью реквизитов id и parentId." +
        "tagsHierarchy следует вернуть на БЭКЕНД - в случае выбора конкретного тега на дереве. ",
        requiredProperties = {"tag", "tagsHierarchy"})
public class ArticleTagsNodeDto {
    private Long id;
    private Long parentId;
    private String tag;
    private int[] tagsHierarchy;
}