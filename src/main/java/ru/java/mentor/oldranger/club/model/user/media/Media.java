package ru.java.mentor.oldranger.club.model.user.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "media")
public class Media {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}) //TODO возможно здесь нужно FetchType.LAZY
    @JoinTable(name = "media_albums", joinColumns = @JoinColumn(name = "media_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id"))
    private List<PhotoAlbum> photoAlbums;
}
