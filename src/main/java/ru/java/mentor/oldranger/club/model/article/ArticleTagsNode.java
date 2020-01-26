package ru.java.mentor.oldranger.club.model.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "article_tags_tree")
public class ArticleTagsNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private ArticleTagsNode parent;

    @Column(name = "position")
    private Integer position;

    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    private ArticleTag tag;

    public ArticleTagsNode(ArticleTagsNode parent, int position, ArticleTag tag) {
        this.parent = parent;
        this.position = position;
        this.tag = tag;
        this.name = UUID.randomUUID().toString();
    }
}
