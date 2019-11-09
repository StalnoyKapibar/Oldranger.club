package ru.java.mentor.oldranger.club.model.forum;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
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

    public Topic(String name, User topicStarter, LocalDateTime startTime, LocalDateTime lastMessageTime, Section section, boolean isHideToAnon) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.section = section;
        this.isHideToAnon = isHideToAnon;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getTopicStarter() {
        return topicStarter;
    }

    public void setTopicStarter(User topicStarter) {
        this.topicStarter = topicStarter;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public boolean isHideToAnon() {
        return isHideToAnon;
    }

    public void setHideToAnon(boolean hideToAnon) {
        isHideToAnon = hideToAnon;
    }
}