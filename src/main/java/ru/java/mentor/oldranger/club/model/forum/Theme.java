package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "themes")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theme_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section")
    private Section section;

    @Column(name = "is_hide", columnDefinition = "TINYINT")
    private boolean isHideToAnon;

    public Theme(String name, Section section, boolean isHideToAnon) {
        this.name = name;
        this.section = section;
        this.isHideToAnon = isHideToAnon;
    }

}
