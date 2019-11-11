package ru.java.mentor.oldranger.club.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserStatistic(User user) {
        this.user = user;
        this.lastVizit = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    public LocalDateTime getLastVizit() {
        return lastVizit;
    }

    public void setLastVizit(LocalDateTime lastVizit) {
        this.lastVizit = lastVizit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}