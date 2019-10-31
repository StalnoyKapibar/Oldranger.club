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
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic_comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_topic")
    private Topic topic;

    @Column(name = "user_comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comment")
    private Comment answerTo;

    @Column(columnDefinition = "DATE", name = "date_comment")
    private LocalDateTime dateTime;

    @Column(name = "text_comment")
    private String commentText;

    public Comment( Topic topic, User user, Comment answerTo, LocalDateTime dateTime, String commentText) {
        this.topic = topic;
        this.user = user;
        this.answerTo = answerTo;
        this.dateTime = dateTime;
        this.commentText = commentText;
    }
}