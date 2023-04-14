package ru.oldranger.club.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oldranger.club.model.user.User;

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

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

}
