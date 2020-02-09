package ru.java.mentor.oldranger.club.model.media;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import ru.java.mentor.oldranger.club.model.user.User;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "photo_album")
@DynamicUpdate
public class PhotoAlbum {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "photo_album_writers",
            joinColumns = {@JoinColumn(name = "album_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> writers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "photo_album_viewers",
            joinColumns = {@JoinColumn(name = "album_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> viewers;

    @Column(name = "allow_view")
    private Boolean allowView;

    @ManyToOne
    private Media media;

    @OneToOne
    @JsonManagedReference
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
