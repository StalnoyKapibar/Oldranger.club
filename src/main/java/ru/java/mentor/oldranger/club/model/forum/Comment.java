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
@Table(name = "comment")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_topic")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comment")
    private Comment answerTo;

    @Column(columnDefinition = "DATE")
    private LocalDateTime dateTime;

    private String commentText;

    public Comment(Long id, Topic topic, User user, Comment answerTo, LocalDateTime dateTime, String commentText) {
        this.id = id;
        this.topic = topic;
        this.user = user;
        this.answerTo = answerTo;
        this.dateTime = dateTime;
        this.commentText = commentText;
    }
}
