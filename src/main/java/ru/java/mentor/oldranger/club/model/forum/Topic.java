package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@ToString
@EqualsAndHashCode
@Table(name = "topics")
public class Topic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_topic", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User topicStarter;

    @Column(name = "date_start")
    private LocalDateTime startTime;

    @Column(name = "date_last_message")
    private LocalDateTime lastMessageTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section")
    private Section section;

    @Column(name = "is_hide", columnDefinition = "TINYINT")
    private boolean isHideToAnon;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TopicVisitAndSubscription> visitAndSubscriptions = new ArrayList<>();

    public Topic(String name, User topicStarter, LocalDateTime startTime, LocalDateTime lastMessageTime, Section section, boolean isHideToAnon) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.section = section;
        this.isHideToAnon = isHideToAnon;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", topicStarter=" + topicStarter +
                ", startTime=" + startTime +
                ", lastMessageTime=" + lastMessageTime +
                ", isHideToAnon=" + isHideToAnon +
                '}';
    }
}