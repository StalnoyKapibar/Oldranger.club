package ru.java.mentor.oldranger.club.model.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "article_tags_tree")
public class ArticleTagsNode {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private ArticleTagsNode parent;

    @Column(name = "position")
    private Integer position;

    @Column(name = "tags_hierarchy")
    private String tagsHierarchy;

    @OneToOne(fetch = FetchType.EAGER)
    private ArticleTag tag;

    public ArticleTagsNode(ArticleTagsNode parent, int position, ArticleTag tag) {
        this.parent = parent;
        this.position = position;
        this.tag = tag;
    }
}
