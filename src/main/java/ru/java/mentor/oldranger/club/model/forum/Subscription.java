package ru.java.mentor.oldranger.club.model.forum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public LocalDateTime getSubscriptionTime() {
        return subscriptionTime;
    }

    public void setSubscriptionTime(LocalDateTime subscriptionTime) {
        this.subscriptionTime = subscriptionTime;
    }

    public LocalDateTime getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(LocalDateTime lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }

    public boolean isHasNewMessages() {
        return hasNewMessages;
    }

    public void setHasNewMessages(boolean hasNewMessages) {
        this.hasNewMessages = hasNewMessages;
    }
}