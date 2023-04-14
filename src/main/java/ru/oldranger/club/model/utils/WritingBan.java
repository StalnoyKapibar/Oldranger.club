package ru.oldranger.club.model.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "writing_ban")
public class WritingBan {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "ban_Type")
    @Enumerated(EnumType.STRING)
    private BanType banType;

    @Column(name = "unlock_time", nullable = true)
    private LocalDateTime unlockTime;

    public WritingBan(User user, BanType banType, LocalDateTime unlockTime) {
        this.user = user;
        this.banType = banType;
        this.unlockTime = unlockTime;
    }
}