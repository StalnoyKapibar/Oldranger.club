package ru.java.mentor.oldranger.club.model.forum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_topic", nullable = false)
    private String name;

    @Column(name = "user_topic")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User topicStarter;

    @Column(columnDefinition = "DATE", name = "date_start")
    private LocalDateTime startTime;

    @Column(columnDefinition = "DATE", name = "date_last_message")
    private LocalDateTime lastMessageTime;

    @Column(name = "section_topic")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_section")
    private Section section;

    @Column(name = "is_hide")
    private boolean isHideToAnon;
}