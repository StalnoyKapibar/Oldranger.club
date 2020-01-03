package ru.java.mentor.oldranger.club.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "media")
public class Media {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

}
