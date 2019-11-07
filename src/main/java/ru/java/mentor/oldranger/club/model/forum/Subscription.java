package ru.java.mentor.oldranger.club.model.forum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_topic", nullable = false)
    private Topic topic;

    @Column(name = "date_subscribed", nullable = false)
    private LocalDateTime subscriptionTime;

    @Column(name = "date_lastvisit")
    private LocalDateTime lastVisitTime;

    @Transient
    private boolean hasNewMessages;

    public Subscription(User user, Topic topic, LocalDateTime subscriptionTime, LocalDateTime lastVisitTime) {
        this.user = user;
        this.topic = topic;
        this.subscriptionTime = subscriptionTime;
        this.lastVisitTime = lastVisitTime;
    }
}
