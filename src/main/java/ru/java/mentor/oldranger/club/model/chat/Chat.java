package ru.java.mentor.oldranger.club.model.chat;

import lombok.*;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_private")
    private boolean privacy = false;

    @Column(name = "token")
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    private PhotoAlbum photoAlbum;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chat_user",
            joinColumns = {@JoinColumn(name = "chat_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> userList;

    public Chat(boolean privacy) {
        this.privacy = privacy;
    }
}