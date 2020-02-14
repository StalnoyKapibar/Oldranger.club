package ru.java.mentor.oldranger.club.model.media;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@Table(name = "photo_album")
@DynamicUpdate
public class PhotoAlbum {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "photo_album_writers",
            joinColumns = {@JoinColumn(name = "album_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> writers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "photo_album_viewers",
            joinColumns = {@JoinColumn(name = "album_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> viewers;

    @Column(name = "allow_view")
    private Boolean allowView;

    @ManyToOne(fetch = FetchType.LAZY)
    private Media media;

    @OneToOne
    private Photo thumbImage;

    public PhotoAlbum(String title) {
        allowView = false;
        this.title = title;
    }

    public void addWriter(User user) {
        if (writers == null) {
            writers = new HashSet<User>();
        }
        writers.add(user);
    }

    public void addViewer(User user) {
        if (viewers == null) {
            viewers = new HashSet<User>();
        }
        viewers.add(user);
    }

}
