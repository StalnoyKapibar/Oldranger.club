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
@Setter
public class ArticleTagsNodeDto {
    private Long id;
    private Long parentId;
    private String  tag;
    private String  tagsHierarchy;

//    public ArticleTagsNodeDto(Long id, Long parentId, String tag, String tagsHierarchy) {
//        this.id = id;
//        this.parentId = parentId;
//        this.tag = tag;
//        this.tagsHierarchy = tagsHierarchy;
//    }

    //    private BigInteger id;
//    private BigInteger parent;
//    private BigInteger position;
//    private BigInteger tag;


//    private String tagsHierarchy;

//    public ArticleTagsNodeDto(BigInteger id, ArticleTagsNode parent, Integer position, ArticleTag tag, String tagsHierarchy) {
//    }


//    public ArticleTagsNodeDto(ArticleTagsNode parent, int position, ArticleTag tag, tagsHierarchy) {
//        this.parent = parent;
//        this.position = position;
//        this.tag = tag;
//    }


//    private Long id;
//    private Long parentId;
//    private ArticleTagsNode parent;
//    private String  tag;
//    private String  tagsHierarchy;
//
//    public ArticleTagsNodeDto(Long id, Long parentId, ArticleTagsNode  parent, String tag, String tagsHierarchy) {
//        this.id = id;
//        this.parentId = parentId;
//        this.parent = parent;
//        this.tag = tag == null ? "NULL" : tag;
//        this.tagsHierarchy = tagsHierarchy;
//    }
}
