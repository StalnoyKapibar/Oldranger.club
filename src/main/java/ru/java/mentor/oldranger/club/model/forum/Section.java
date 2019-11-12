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
    private boolean isHideToAnon;

    public Section(String name, int position, boolean isHideToAnon) {
        this.name = name;
        this.position = position;
        this.isHideToAnon = isHideToAnon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isHideToAnon() {
        return isHideToAnon;
    }

    public void setHideToAnon(boolean hideToAnon) {
        isHideToAnon = hideToAnon;
    }
}