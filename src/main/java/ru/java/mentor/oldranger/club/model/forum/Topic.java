package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
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
@Indexed
@EqualsAndHashCode
@Table(name = "topics")
public class Topic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    @Column(name = "name_topic", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User topicStarter;

    @Column(name = "message_count")
    private long messageCount;

    @Column(name = "date_start")
    private LocalDateTime startTime;

    @Column(name = "date_last_message")
    private LocalDateTime lastMessageTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsection_id")
    private Subsection subsection;

    @Column(name = "is_hide", columnDefinition = "TINYINT")
    private boolean isHideToAnon;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TopicVisitAndSubscription> visitAndSubscriptions = new ArrayList<>();

    public Topic(String name, User topicStarter, LocalDateTime startTime, LocalDateTime lastMessageTime, Subsection subsection, boolean isHideToAnon) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.subsection = subsection;
        this.isHideToAnon = isHideToAnon;
    }

    public Section getSection() {
        return getSubsection().getSection();
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

    public boolean isHideToAnon() {
        return isHideToAnon;
    }
}