package ru.java.mentor.oldranger.club.model.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "article_tags_tree")
public class ArticleTagsNode {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent")
    private ArticleTagsNode parent;

    @Column(name = "order")
    private Integer order;

    @OneToOne(fetch = FetchType.EAGER)
    private ArticleTag tag;
}
