package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString
@Entity
@NoArgsConstructor
@Table(name = "topic_visit_and_subscriptions")
public class TopicVisitAndSubscription {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "is_subscribed", columnDefinition = "TINYINT", nullable = false)
    private boolean isSubscribed;

    @Column(name = "date_subscribed")
    private LocalDateTime subscriptionTime;

    @Column(name = "date_lastvisit")
    private LocalDateTime lastVisitTime;

    @Transient
    private boolean hasNewMessages;

    public TopicVisitAndSubscription(User user, Topic topic, boolean isSubscribed) {
        this.user = user;
        this.topic = topic;
        this.isSubscribed = isSubscribed;
    }

    public TopicVisitAndSubscription(User user, Topic topic, boolean isSubscribed, LocalDateTime subscriptionTime) {
        this.user = user;
        this.topic = topic;
        this.isSubscribed = isSubscribed;
        this.subscriptionTime = subscriptionTime;
    }

    public TopicVisitAndSubscription(User user, Topic topic, boolean isSubscribed, LocalDateTime subscriptionTime, LocalDateTime lastVisitTime) {
        this.user = user;
        this.topic = topic;
        this.isSubscribed = isSubscribed;
        this.subscriptionTime = subscriptionTime;
        this.lastVisitTime = lastVisitTime;
    }
}
