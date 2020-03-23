package ru.java.mentor.oldranger.club.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Indexed
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseComment{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_topic")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comment")
    private Comment answerTo;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @Transient
    private boolean pozition;

    public Comment(Topic topic, User user, Comment answerTo, LocalDateTime dateTime, String commentText) {
        super(user, dateTime, commentText);
        this.topic = topic;
        this.answerTo = answerTo;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + getId() +
                ", topic=" + topic +
                ", user=" + getUser() +
                ", answerTo=" + ((answerTo == null) ? "NULL" : answerTo) +
                ", dateTime=" + this.getDateTime() +
                ", commentText='" + this.getCommentText() + '\'' +
                ", pozition=" + pozition +
                ", pozition=" + isDeleted +
                '}';
    }
}