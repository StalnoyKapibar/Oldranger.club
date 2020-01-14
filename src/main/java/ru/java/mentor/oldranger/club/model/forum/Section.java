package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "sections")
public class Section {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_section", nullable = false)
    private String name;

    @Column(name = "position")
    private int position;

    @Column(name = "is_hide", columnDefinition = "TINYINT")
    private boolean hideToAnon;

    public Section(String name, int position, boolean hideToAnon) {
        this.name = name;
        this.position = position;
        this.hideToAnon = hideToAnon;
    }
}