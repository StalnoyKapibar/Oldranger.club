package ru.java.mentor.oldranger.club.model.article;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags")
public class ArticleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tag_name", unique = true, nullable = false)
    private String name;

    public ArticleTag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
