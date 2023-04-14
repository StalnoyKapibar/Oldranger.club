package ru.oldranger.club.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_statistic")
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
    private LocalDateTime lastVisit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UserStatistic(User user) {
        this.user = user;
        this.lastVisit = LocalDateTime.now();
    }
}

