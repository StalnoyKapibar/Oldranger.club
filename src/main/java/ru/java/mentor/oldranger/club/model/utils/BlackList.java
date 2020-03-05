package ru.java.mentor.oldranger.club.model.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "black_list")
public class BlackList {

    @Id
    @Column(name = "id_black_list")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "unlock_time", nullable = true)
    private LocalDateTime unlockTime;

    public BlackList(User user, LocalDateTime unlockTime) {
        this.user = user;
        this.unlockTime = unlockTime;
    }
}