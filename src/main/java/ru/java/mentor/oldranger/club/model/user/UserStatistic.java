package ru.java.mentor.oldranger.club.model.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userStatistic")
public class UserStatistic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_count")
    private long messageCount;

    @Column(name = "topics_count")
    private long topicStartCount;

    @Column(name = "last_comment")
    private LocalDateTime lastComment;

    @Column(name = "last_vizit")
    private LocalDateTime lastVizit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UserStatistic(User user) {
        this.user = user;
        this.lastVizit = LocalDateTime.now();
    }
}