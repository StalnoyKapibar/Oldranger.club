package ru.java.mentor.oldranger.club.model.article;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "article_section")
public class ArticlesSection {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_section_name", nullable = false)
    private String name;

    public ArticlesSection(String name) {
        this.name = name;
    }


}
