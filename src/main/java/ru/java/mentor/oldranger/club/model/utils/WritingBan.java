package ru.java.mentor.oldranger.club.model.utils;

import lombok.*;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "writing_ban")
public class WritingBan {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "banType")
    @Enumerated(EnumType.STRING)
    private BanType banType;

    @Column(name = "unlock_time", nullable = true)
    private LocalDateTime unlockTime;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_ban", nullable = false)
//    private Ban ban;

    public WritingBan(User user, BanType banType, LocalDateTime unlockTime) {
        this.user = user;
        this.banType = banType;
        this.unlockTime = unlockTime;
    }
}