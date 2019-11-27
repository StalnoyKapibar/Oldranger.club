package ru.java.mentor.oldranger.club.model.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_img")
    private String original;

    @Column(name = "small_img")
    private String small;

    @Column(name = "description")
    private String description;

    public Photo(String original, String small) {
        this.original = original;
        this.small = small;
    }
}
