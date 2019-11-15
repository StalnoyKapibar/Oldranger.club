package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "subsections")
public class Subsection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subsection_name")
    private String name;

    @Column(name = "position")
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section")
    private Section section;

    @Column(name = "is_hide", columnDefinition = "TINYINT")
    private boolean isHideToAnon;

    public Subsection(String name, int position, Section section, boolean isHideToAnon) {
        this.name = name;
        this.position = position;
        this.section = section;
        this.isHideToAnon = isHideToAnon;
    }
}
