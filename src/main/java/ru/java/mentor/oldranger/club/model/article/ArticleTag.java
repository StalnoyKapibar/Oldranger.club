package ru.java.mentor.oldranger.club.model.article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags", uniqueConstraints = { @UniqueConstraint(columnNames = { "parent_id", "tag_name" }) } )
public class ArticleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="parent_id", nullable = true)
    private ArticleTag parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private List<ArticleTag> subTags;

    @Column(name = "tag_name", nullable = false)
    private String name;

    public ArticleTag(String name) {
        this.name = name;
    }

    public ArticleTag(String name, ArticleTag parent) {
        this.parent = parent;
        this.name = name;
    }

    @JsonProperty("parent")
    private void getParent(long parent_id) {
        if (parent_id == 0) {
            this.parent = null;
            return;
        }
        this.parent = new ArticleTag();
        parent.setId(parent_id);
    }
}
