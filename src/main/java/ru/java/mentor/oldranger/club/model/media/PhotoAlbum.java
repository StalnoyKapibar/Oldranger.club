package ru.java.mentor.oldranger.club.model.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "photoAlbum")
@DynamicUpdate
public class PhotoAlbum {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    private Media media;

    public PhotoAlbum(String title) {
        this.title = title;
    }
}
