package ru.java.mentor.oldranger.club.model.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Indexed
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long positionInTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_topic")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comment")
    private Comment answerTo;

    @Column(columnDefinition = "DATETIME", name = "date_comment")
    private LocalDateTime dateTime;

    @Field
    @Type(type = "materialized_clob")
    @Column(name = "text_comment")
    private String commentText;

    @Transient
    private boolean pozition;

    public Comment(Topic topic, User user, Comment answerTo, LocalDateTime dateTime, String commentText) {
        this.topic = topic;
        this.user = user;
        this.answerTo = answerTo;
        this.dateTime = dateTime;
        this.commentText = commentText;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", topic=" + topic +
                ", user=" + user +
                ", answerTo=" + ((answerTo == null) ? "NULL" : answerTo) +
                ", dateTime=" + dateTime +
                ", commentText='" + commentText + '\'' +
                ", pozition=" + pozition +
                '}';
    }
}